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
package com.eggze.spreadit.common.packet.server.user;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import com.eggze.spreadit.common.packet.server.results.SendResultsPacketIF;
import com.eggze.spreadit.common.util.UUIDUtil;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 *
 * The ServerHelloPacket extends the ServerPacket implementation.
 * It contains the packet type, the client's packet index (as a reply),
 * the user's UUID, the communication protocol, whether the user is suspected,
 * whether the user is infected.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ServerHelloPacket extends ServerPacket implements ServerHelloPacketIF {
    
    private short contacts = Short.MAX_VALUE; // Max value is invalid
    private byte testResult  = SendResultsPacketIF.RES_INVALID;
    
    protected final byte[] userUUID;

    public ServerHelloPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
        this.userUUID = new byte[16];
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        //byte[] bytes = source.array();
        source.get();// byte[0] is type, ignore
        //this.protocolVersion = bytes[1];
        this.protocolVersion = source.get();
        this.packetIndex = source.getShort();
        this.contacts = source.getShort();
        this.testResult = source.get();
        //this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
        //                    | ((bytes[3] & 0xFF)));//
        //this.suspected =  (short)(((bytes[4] & 0xFF) << 8)//
        //                    | ((bytes[5] & 0xFF))); // If the user is suspected
        //this.result = bytes[6]; // If the user is infected
        
        //System.out.println("RECONSTRUCT Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " Version: " + bytes[1]
        //        + " Suspected: " + this.suspected);
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1  // type
                + 1  // protocol version
                + 2 // packet index
                + 2 // suspected
                + 1; // result

        bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = SpreaditPacket.TYPE_SERVER;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ServerPacketIF.T_SERVER_HELLO;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        bytes[7] = (byte) (this.contacts >>> 8);//
        bytes[8] = (byte) (this.contacts);
        bytes[9] = this.testResult;
        return ByteBuffer.wrap(bytes);
    }
    
    @Override
    public short getContacts(){
        return this.contacts;
    }
    
    @Override
    public void setContacts(short contacts){
        this.contacts = contacts;
    }

    @Override
    public byte getTestResult() {
        return this.testResult;
    }

    @Override
    public void setTestResult(byte testResult) {
        this.testResult = testResult;
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_SERVER_HELLO;
    }
    
        @Override
    public void setUserUUID(UUID userUUID) {
        System.arraycopy(UUIDUtil.toBytes(userUUID), 0, this.userUUID, 0, 16);
    }

    @Override
    public void setUserUUID(byte[] userUUID) {
        System.arraycopy(userUUID, 0, this.userUUID, 0, 16);
    }

    @Override
    public byte[] getUserUUIDBytes() {
        return this.userUUID;
    }

    @Override
    public UUID getUserUUID() {
        return UUIDUtil.toUUID(this.userUUID);
    }

    public static void main(String[] args) {
        ServerHelloPacket nup = new ServerHelloPacket(null, null);
        nup.setProtocolVersion((byte)1);
        nup.setContacts((short)0);

        ServerHelloPacket nup2 = new ServerHelloPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);

        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
    }

}
