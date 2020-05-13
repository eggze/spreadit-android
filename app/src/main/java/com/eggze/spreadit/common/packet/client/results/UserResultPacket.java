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
package com.eggze.spreadit.common.packet.client.results;

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
public class UserResultPacket extends ClientPacket implements UserResultPacketIF {

    private long resultTimestamp;
    private byte[] resultUUID;

    public UserResultPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
        this.userUUID = new byte[16];
        this.resultUUID = new byte[16];
    }

    @Override
    public long getResultTimestamp(){
        return this.resultTimestamp;
    }

    @Override
    public void setResultTimestamp(long resultTimestamp){
        this.resultTimestamp = resultTimestamp;
    }

    @Override
    public byte getClientPacketType() {
        return ClientPacketIF.T_USER_RESULT;
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        byte[] bytes = source.array();
        // byte[0] is type, ignore
        this.protocolVersion = bytes[1];
        this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
                            | ((bytes[3] & 0xFF)));//
        System.arraycopy(bytes, 4, this.userUUID, 0, 16); // Index 17 user
        System.arraycopy(bytes, 20, this.resultUUID, 0, 16); // Index 33 loc
        resultTimestamp = ((bytes[36] & 0xFFL) << 56)//
                | ((bytes[37] & 0xFFL) << 48)//
                | ((bytes[38] & 0xFFL) << 40)//
                | ((bytes[39] & 0xFFL) << 32)//
                | ((bytes[40] & 0xFFL) << 24)//
                | ((bytes[41] & 0xFFL) << 16)//
                | ((bytes[42] & 0xFFL) << 8)//
                | ((bytes[43] & 0xFFL));//
        
        //System.out.println("RECONSTRUCT Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " User UUID: " + UUIDUtil.toUUID(this.userUUID).toString()
        //        + " Loc UUID: " + UUIDUtil.toUUID(this.resultUUID).toString()
        //        + " Timestamp: " + this.resultTimestamp
        //        + " Test Result: " + this.result);
    }

    @Override
    public ByteBuffer toBytes() {
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                +16 // Length of userUUID (UUID, 16 bytes long)
                +16 // Length of resultUID (UUID, 16 bytes long)
                + 8; // Length of timestamp
        byte[] bytes = new byte[3 + len]; // Data + header
        
        //System.out.println("TOBYTES Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " User UUID: " + UUIDUtil.toUUID(this.userUUID).toString()
        //        + " Result UUID: " + UUIDUtil.toUUID(this.resultUUID).toString()
        //        + " Timestamp: " + this.resultTimestamp
        //        + " Result: " + this.resultResult);
        
        bytes[0] = SpreaditPacket.TYPE_CLIENT; // type
        bytes[1] = (byte) ((len >> 8) & 0xFF); // length
        bytes[2] = (byte) (len & 0xFF); // length
        bytes[3] = ClientPacketIF.T_USER_RESULT; // type
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        System.arraycopy(this.userUUID, 0, bytes, 7, 16); // user uuid
        System.arraycopy(this.resultUUID, 0, bytes, 23, 16); // loc uuid
        bytes[39] = (byte) (resultTimestamp >>> 56);//
        bytes[40] = (byte) (resultTimestamp >>> 48);//
        bytes[41] = (byte) (resultTimestamp >>> 40);//
        bytes[42] = (byte) (resultTimestamp >>> 32);//
        bytes[43] = (byte) (resultTimestamp >>> 24);//
        bytes[44] = (byte) (resultTimestamp >>> 16);//
        bytes[45] = (byte) (resultTimestamp >>> 8);//
        bytes[46] = (byte) (resultTimestamp);//

        return ByteBuffer.wrap(bytes);
    }
    
    @Override
    public void setResultUUID(UUID resultUUID) {
        System.arraycopy(UUIDUtil.toBytes(resultUUID), 0, this.resultUUID, 0, 16);
    }

    @Override
    public void setResultUUID(byte[] resultUUID) {
        System.arraycopy(resultUUID, 0, this.resultUUID, 0, 16);
    }

    @Override
    public byte[] getResultUUIDBytes() {
        return this.resultUUID;
    }

    @Override
    public UUID getResultUUID() {
        return UUIDUtil.toUUID(this.resultUUID);
    }
    
    public static void main(String[] args){
        UserResultPacket nup = new UserResultPacket(null, null);
        
        UUID userUUID = UUID.randomUUID();
        UUID resultUUID = UUID.randomUUID();
        System.out.println("Setting userUUID " + userUUID + " resultUUID " + resultUUID);
        nup.setUserUUID(UUIDUtil.toBytes(userUUID));
        nup.setResultUUID(UUIDUtil.toBytes(resultUUID));
        nup.setResultTimestamp(System.currentTimeMillis());
        
        UserResultPacket nup2 = new UserResultPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);
    }
    
}