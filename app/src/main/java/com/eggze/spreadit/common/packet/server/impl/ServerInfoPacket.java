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

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ServerInfoPacket extends ServerPacket implements ServerInfoPacketIF{
    
    private short infoID;
    
    public ServerInfoPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        byte[] bytes = source.array();
        // bytes[0] is type, IGNORE
        this.protocolVersion = bytes[1];
        this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
                            | ((bytes[3] & 0xFF)));//
        this.infoID = (short)((bytes[4] & 0xFF) << 8
                | (bytes[5] & 0xFF));
        
        //System.out.println("ID:" + infoID + ": " + getInfoDescription(infoID));
    }

    @Override
    public ByteBuffer toBytes() {
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 2; // info id
        byte[] bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = ServerPacket.TYPE_SERVER;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ServerPacketIF.T_SERVER_INFO;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        bytes[7] = (byte) ((infoID >> 8) & 0xFF);
        bytes[8] = (byte) (infoID & 0xFF);
        
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_SERVER_INFO;
    }

    @Override
    public void setInfoID(short infoID) {
        this.infoID = infoID;
    }

    @Override
    public short getInfoID() {
        return this.infoID;
    }
    
    public static String getInfoDescription(int infoID){
        if(infoID < 0 || infoID > ServerInfoPacketIF.I_NAMES.length){
            return "Invalid Info ID";
        }
        return ServerInfoPacketIF.I_NAMES[infoID];
    }
    
    public static void main(String[] args){
        ServerInfoPacket sep = new ServerInfoPacket(null, null);
        //sep.setInfoID(ServerErrorPacketIF.INFO_USER_NOT_EXISTS);
        sep.toBytes();
        sep.setInfoID(ServerInfoPacketIF.INFO_UNKNOWN);
            String test = StandardCharsets.UTF_8.decode(sep.toBytes()).toString();
            byte[] bytes = sep.toBytes().array();
            for(byte b:bytes){
                System.out.print(Byte.toUnsignedInt(b) + ",");
                }
            System.out.println();
        
        ServerInfoPacket sep2 = new ServerInfoPacket(null, null);
        sep2.reconstructPadded(sep.toBytes(), false);
    }
    
}
