/**
 *  This file is part of spreadit-backend. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-backend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-backend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.common.packet.client.scans;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.packet.client.ClientPacket;
import com.eggze.spreadit.common.util.UUIDUtil;
import java.nio.ByteBuffer;
import java.util.UUID;
import com.eggze.spreadit.common.packet.client.ClientPacketIF;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class UserScanPacket extends ClientPacket implements UserScanPacketIF {

    private long scanTimestamp;
    protected byte[] locUUID;
    protected byte[] scanUUID;
    private byte traceStatus = Byte.MAX_VALUE; // Default invalid

    public UserScanPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
        this.userUUID = new byte[16];
        this.locUUID = new byte[16];
        this.scanUUID = new byte[16];
    }
    
    @Override
    public long getScanTimestamp(){
        return this.scanTimestamp;
    }
    
    @Override
    public void setScanTimestamp(long scanTimestamp){
        this.scanTimestamp = scanTimestamp;
    }

    @Override
    public byte getClientPacketType() {
        return ClientPacketIF.T_USER_SCAN;
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        byte[] bytes = source.array();
        // byte[0] is type, ignore
        this.protocolVersion = bytes[1];
        this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
                            | ((bytes[3] & 0xFF)));//
        traceStatus = bytes[4];
        System.arraycopy(bytes, 5, this.scanUUID, 0, 16); // Index 18 user
        System.arraycopy(bytes, 21, this.userUUID, 0, 16); // Index 34 user
        System.arraycopy(bytes, 37, this.locUUID, 0, 16); // Index 50 loc
        scanTimestamp = ((bytes[53] & 0xFFL) << 56)//
                        | ((bytes[54] & 0xFFL) << 48)//
                        | ((bytes[55] & 0xFFL) << 40)//
                        | ((bytes[56] & 0xFFL) << 32)//
                        | ((bytes[57] & 0xFFL) << 24)//
                        | ((bytes[58] & 0xFFL) << 16)//
                        | ((bytes[59] & 0xFFL) << 8)//
                        | ((bytes[60] & 0xFFL));//
        
        //System.out.println("RECONSTRUCT Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " Index: " + this.packetIndex
        //        + " Infection status " + bytes[3]
        //        + " Scan UUID: " + UUIDUtil.toUUID(this.scanUUID).toString()
        //        + " User UUID: " + UUIDUtil.toUUID(this.userUUID).toString()
        //        + " Loc UUID: " + UUIDUtil.toUUID(this.locUUID).toString()
        //        + " Timestamp: " + this.scanTimestamp);
    }

    @Override
    public ByteBuffer toBytes() {
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 1 // Trace status
                +16 // Length of scanUUID (UUID, 16 bytes long)
                +16 // Length of userUUID (UUID, 16 bytes long)
                +16 // Length of locUUID (UUID, 16 bytes long)
                + 8; // Length of timestamp
        byte[] bytes = new byte[3 + len]; // Data + header
        
        //System.out.println("TOBYTES Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " Index: " + packetIndex
        //        + " User UUID: " + UUIDUtil.toUUID(this.userUUID).toString()
        //        + " Loc UUID: " + UUIDUtil.toUUID(this.locUUID).toString()
        //        + " Timestamp: " + this.scanTimestamp);
        
        bytes[0] = SpreaditPacket.TYPE_CLIENT; // type
        bytes[1] = (byte) ((len >> 8) & 0xFF); // length
        bytes[2] = (byte) (len & 0xFF); // length
        bytes[3] = ClientPacketIF.T_USER_SCAN; // type
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        bytes[7] = traceStatus; // traceStatus
        System.arraycopy(this.scanUUID, 0, bytes, 8, 16); // user uuid
        System.arraycopy(this.userUUID, 0, bytes, 24, 16); // user uuid
        System.arraycopy(this.locUUID, 0, bytes, 40, 16); // loc uuid
        bytes[56] = (byte) (scanTimestamp >>> 56);//
        bytes[57] = (byte) (scanTimestamp >>> 48);//
        bytes[58] = (byte) (scanTimestamp >>> 40);//
        bytes[59] = (byte) (scanTimestamp >>> 32);//
        bytes[60] = (byte) (scanTimestamp >>> 24);//
        bytes[61] = (byte) (scanTimestamp >>> 16);//
        bytes[62] = (byte) (scanTimestamp >>> 8);//
        bytes[63] = (byte) (scanTimestamp);//

        return ByteBuffer.wrap(bytes);
    }
    
    @Override
    public void setLocUUID(UUID locUUID) {
        System.arraycopy(UUIDUtil.toBytes(locUUID), 0, this.locUUID, 0, 16);
    }

    @Override
    public void setLocUUID(byte[] locUUID) {
        System.arraycopy(locUUID, 0, this.locUUID, 0, 16);
    }

    @Override
    public byte[] getLocUUIDBytes() {
        return this.locUUID;
    }

    @Override
    public UUID getLocUUID() {
        return UUIDUtil.toUUID(this.locUUID);
    }
    
    @Override
    public void setScanUUID(UUID scanUUID) {
        System.arraycopy(UUIDUtil.toBytes(scanUUID), 0, this.scanUUID, 0, 16);
    }

    @Override
    public void setScanUUID(byte[] scanUUID) {
        System.arraycopy(scanUUID, 0, this.scanUUID, 0, 16);
    }

    @Override
    public byte[] getScanUUIDBytes() {
        return this.scanUUID;
    }

    @Override
    public UUID getScanUUID() {
        return UUIDUtil.toUUID(this.scanUUID);
    }

    @Override
    public byte getScanTraceStatus() {
        return this.traceStatus;
    }

    @Override
    public void setScanTraceStatus(byte traceStatus) {
        this.traceStatus = traceStatus;
    }
    
    public static void main(String[] args){
        UserScanPacket nup = new UserScanPacket(null, null);
        
        UUID userUUID = UUID.randomUUID();
        UUID locUUID = UUID.randomUUID();
        UUID scanUUID = UUID.randomUUID();
        System.out.println("Setting scanUUID " + scanUUID + " userUUID " + userUUID + " locUUID " + locUUID);
        nup.setUserUUID(userUUID);
        nup.setLocUUID(locUUID);
        nup.setScanUUID(scanUUID);
        nup.setScanTraceStatus((byte)12);
        nup.setScanTimestamp(System.currentTimeMillis());
        
        UserScanPacket nup2 = new UserScanPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);
        
    }
}