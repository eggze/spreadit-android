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
package com.eggze.spreadit.common.packet.server.scans;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import java.nio.ByteBuffer;

/**
 * A packet indicating scans with contacts have been received by the client.
 * Upon reception of this packet, the spreadit backend sets the SENT flag of
 * these scans to 1, indicating that the client has received them and they are
 * not to be sent again (unless the number of contacts for each of these scans
 * has changed in a subsequent periodic update).
 * 
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class DatesReceivedOKPacket extends ServerPacket implements DatesReceivedOKPacketIF {

    public DatesReceivedOKPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        byte[] bytes = source.array();
        // byte[0] is type, ignore
        this.protocolVersion = bytes[1];
        this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
                            | ((bytes[3] & 0xFF)));//
        //System.out.println("RECONSTRUCT Length: " + bytes.length 
        //        + " Type: " + bytes[0]);
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1  // type
                + 1 // protocol version
                + 2; // packet index

        bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = SpreaditPacket.TYPE_SERVER;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ServerPacketIF.T_DATES_RECEIVED_OK;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_DATES_RECEIVED_OK;
    }

    public static void main(String[] args) {
        DatesReceivedOKPacket nup = new DatesReceivedOKPacket(null, null);


        DatesReceivedOKPacket nup2 = new DatesReceivedOKPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);

        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
    }

}
