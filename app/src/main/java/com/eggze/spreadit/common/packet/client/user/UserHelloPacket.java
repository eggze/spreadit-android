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
package com.eggze.spreadit.common.packet.client.user;

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
public class UserHelloPacket extends ClientPacket implements UserHelloPacketIF {

    public UserHelloPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
        this.userUUID = new byte[16];
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        byte[] bytes = source.array();
        // byte[0] is type, ignore
        this.protocolVersion = bytes[1];
        this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
                            | ((bytes[3] & 0xFF)));//
        
        System.arraycopy(bytes, 4, this.userUUID, 0, 16); // Index 18 user
        
        //System.out.println("RECONSTRUCT Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " Version: " + bytes[1]
        //        + " User UUID: " + UUIDUtil.toUUID(this.userUUID).toString());
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 16; // userUUID

        bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = SpreaditPacket.TYPE_CLIENT;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ClientPacketIF.T_USER_HELLO;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        System.arraycopy(this.userUUID, 0, bytes, 7, 16); // user uuid
        return ByteBuffer.wrap(bytes);
    }
    


    @Override
    public byte getClientPacketType() {
        return ClientPacketIF.T_USER_HELLO;
    }

    public static void main(String[] args) {
        UserHelloPacket nup = new UserHelloPacket(null, null);
        nup.setProtocolVersion((byte)0);
        UUID uuid = UUID.randomUUID();
        System.out.println("Setting UUID " + uuid);
        nup.setUserUUID(UUIDUtil.toBytes(uuid));

        UserHelloPacket nup2 = new UserHelloPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);

        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
    }

}
