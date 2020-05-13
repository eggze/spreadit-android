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
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class UserCreatedPacket extends ServerHelloPacket implements UserCreatedPacketIF{
    
    // TODO THROW UNSUPPORTED EXCEPTION ON isSuspected()!
    
    public UserCreatedPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        // BYTES 0 DENOTE THE TYPE BUT WE DONT CARE AS WE KNOW IT
        //byte[] bytes = source.array();
        // Only payload is UID and protocol version
        //this.protocolVersion = bytes[1];
        //this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
        //                    | ((bytes[3] & 0xFF)));//
        source.get(); // first byte is the packet type
        this.protocolVersion = source.get();
        this.packetIndex =  (short)(((source.get() & 0xFF) << 8)//
                            | ((source.get() & 0xFF)));//
        byte[] tmp = new byte[16];
        source.get(tmp);
        System.arraycopy(tmp, 0, this.userUUID, 0, 16);
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 16; // Length of UUID (UUID, 16 bytes long)
        
        bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = ServerPacket.TYPE_SERVER;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ServerPacketIF.T_USER_CREATED;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        
        System.arraycopy(this.userUUID, 0, bytes, 7, 16);
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_USER_CREATED;
    }
    
    public static void main(String[] args){
        UserCreatedPacket ucp = new UserCreatedPacket(null, null);
        ucp.setUserUUID(UUID.randomUUID());
        
        UserCreatedPacket ucp2 = new UserCreatedPacket(null, null);
        ucp2.reconstructPadded(ucp.toBytes(), false);
    }
    
}
