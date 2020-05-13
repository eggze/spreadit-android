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
import com.eggze.spreadit.common.packet.client.ClientPacketIF;
import java.util.UUID;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ReqDatesPacket extends ClientPacket implements ReqDatesPacketIF {

    public ReqDatesPacket(SenderIF sender, SocketIF socket) {
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
        //lastSentTimestamp = ((bytes[4] & 0xFFL) << 56)//
        //        | ((bytes[5] & 0xFFL) << 48)//
        //        | ((bytes[6] & 0xFFL) << 40)//
        //        | ((bytes[7] & 0xFFL) << 32)//
        //        | ((bytes[8] & 0xFFL) << 24)//
        //        | ((bytes[9] & 0xFFL) << 16)//
        //        | ((bytes[10] & 0xFFL) << 8)//
        //        | ((bytes[11] & 0xFFL));//
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
                //+ 8 // last sent timestamp
                + 16; // userUUID
        

        bytes = new byte[len + 3]; // Including header + message length
        bytes[0] = SpreaditPacket.TYPE_CLIENT;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ClientPacketIF.T_REQ_DATES;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        //bytes[7] = (byte) (lastSentTimestamp >>> 56);//
        //bytes[8] = (byte) (lastSentTimestamp >>> 48);//
        //bytes[9] = (byte) (lastSentTimestamp >>> 40);//
        //bytes[10] = (byte) (lastSentTimestamp >>> 32);//
        //bytes[11] = (byte) (lastSentTimestamp >>> 24);//
        //bytes[12] = (byte) (lastSentTimestamp >>> 16);//
        //bytes[13] = (byte) (lastSentTimestamp >>> 8);//
        //bytes[14] = (byte) (lastSentTimestamp);//
        
        System.arraycopy(this.userUUID, 0, bytes, 7, 16); // user uuid
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public byte getClientPacketType() {
        return ClientPacketIF.T_REQ_DATES;
    }

    public static void main(String[] args) {
        ReqDatesPacket nup = new ReqDatesPacket(null, null);

        UUID uuid = UUID.randomUUID();
        //System.out.println("Setting UUID " + uuid);
        nup.setUserUUID(UUIDUtil.toBytes(uuid));

        ReqDatesPacket nup2 = new ReqDatesPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);

        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
    }

    //@Override
    //public long getLastSentTimestamp() {
    //    return 0;
        //return this.lastSentTimestamp;
    //}

    //@Override
    //public void setLastSentTimestamp(long lastSentTimestamp) {
        //this.lastSentTimestamp = lastSentTimestamp;
    //}

}
