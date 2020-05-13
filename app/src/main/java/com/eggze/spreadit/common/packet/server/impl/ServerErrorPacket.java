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
package com.eggze.spreadit.common.packet.server.impl;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import com.eggze.spreadit.common.packet.client.ClientPacketIF;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ServerErrorPacket extends ServerPacket implements ServerErrorPacketIF{
    
    private short errorID = E_INVALID;
    private short clientPacketType = ClientPacketIF.T_INVALID;
    
    public ServerErrorPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        byte[] bytes = source.array();
        // bytes[0] is type, IGNORE
        this.protocolVersion = bytes[1];
        this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
                            | ((bytes[3] & 0xFF)));//
        this.clientPacketType = (short)((bytes[4] & 0xFF) << 8
                     | (bytes[5] & 0xFF));
        this.errorID = (short)((bytes[6] & 0xFF) << 8
                     | (bytes[7] & 0xFF));
        
        //System.out.println("Error ID:" + errorID + ": " + getErrorDescription(errorID));
    }

    @Override
    public ByteBuffer toBytes() {
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 2 // user packet type
                + 2; // info id
        byte[] bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = ServerPacket.TYPE_SERVER;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ServerPacketIF.T_SERVER_ERROR;
        bytes[4] = protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        bytes[7] = (byte) (this.clientPacketType >>> 8);//
        bytes[8] = (byte) (this.clientPacketType);
        bytes[9] = (byte) ((this.errorID >> 8) & 0xFF);
        bytes[10] = (byte) (this.errorID & 0xFF);
        
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_SERVER_ERROR;
    }

    @Override
    public void setErrorID(short errorID) {
        this.errorID = errorID;
    }

    @Override
    public short getErrorID() {
        return this.errorID;
    }
    
    public static String getErrorDescription(int errorID){
        if(errorID < 0 || errorID > ServerErrorPacketIF.E_NAMES.length){
            return "Invalid Error ID";
        }
        return ServerErrorPacketIF.E_NAMES[errorID];
    }
    
    public static void main(String[] args){
        ServerErrorPacket sep = new ServerErrorPacket(null, null);
        //sep.setInfoID(ServerErrorPacketIF.INFO_USER_NOT_EXISTS);
        sep.toBytes();
        sep.setErrorID(ServerErrorPacketIF.E_LOC_INVALID);
        sep.setProtocolVersion((byte)123);
            String test = StandardCharsets.UTF_8.decode(sep.toBytes()).toString();
            byte[] bytes = sep.toBytes().array();
            for(byte b:bytes){
                System.out.print(Byte.toUnsignedInt(b) + ",");
                }
            System.out.println();
        
        ServerErrorPacket sep2 = new ServerErrorPacket(null, null);
        sep2.reconstructPadded(sep.toBytes(), false);
    }

    @Override
    public short getClientPacketType() {
        return this.clientPacketType;
    }

    @Override
    public void setClientPacketType(short clientPacketType) {
        this.clientPacketType = clientPacketType;
    }
    
}
