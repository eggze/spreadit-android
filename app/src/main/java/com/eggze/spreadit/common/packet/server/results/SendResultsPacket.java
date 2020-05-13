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
package com.eggze.spreadit.common.packet.server.results;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import com.eggze.spreadit.common.util.UUIDUtil;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.UUID;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class SendResultsPacket extends ServerPacket implements SendResultsPacketIF {

    ArrayDeque<ResultPacket> resultPackets;

    public SendResultsPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
        resultPackets = new ArrayDeque<>(10); // Initial capacity 10
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        //System.out.println("Source before get, pos: " + source.position()
        //        + " lim " + source.limit()
        //        + " cap " + source.capacity());
        source.get(); // First byte is type, ignore
        this.protocolVersion = source.get();
        this.packetIndex = source.getShort();
        //this.packetIndex = (short) (((source.get() & 0xFF) << 8)//
        //        | ((source.get() & 0xFF)));//
        // Next byte is the number of packets contained within this packet
        int packets = 0;
        try{
            packets = source.get();
        }catch(BufferUnderflowException bue){
            // No contained packets
            return;
        }
        if (packets == 0) {
            //System.out.println("No contained packets");
            // No contained packets
            return;
        }
        //System.out.println("Packet no: " + packets);
        for (int i = 0; i < packets; i++) {
            byte[] tmp = new byte[16]; // UUID size 16 in bytes
            source.get(tmp);
            byte tmp2 = source.get(); // the result byte
            long resultDate = source.getLong();
            resultPackets.add(new ResultPacket(UUIDUtil.toUUID(tmp), tmp2, resultDate));
        }
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 1; // number of contained packets (1 byte)

        int packetCount = resultPackets.size();
        bytes = new byte[len + 3]; // + size of header
        bytes[0] = SpreaditPacket.TYPE_SERVER;
        // Dont assign the length yet as we dont know it
        bytes[3] = ServerPacketIF.T_SEND_RESULTS;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        bytes[7] = (byte) (packetCount & 0xFF);
        if (packetCount == 0) {
            // No results, assign the length
            bytes[1] = (byte) ((len >> 8) & 0xFF);
            bytes[2] = (byte) (len & 0xFF);
            // And return the packet
            return ByteBuffer.wrap(bytes);
        }
        // Otherwise lets do the size manually
        int maxTotalSize = bytes.length + 25 * packetCount; // Size of UUID in bytes is 16 + 8 + 1 byte for the actual result
        //System.out.println("Max size allocated " + maxTotalSize + " bytes");
        // Allocate a bytebuffer big enough
        ByteBuffer bb = ByteBuffer.allocate(maxTotalSize);
        // add the header
        bb.put(bytes);
        int pLength = 0;
        // add all the data from the packets
        for (ResultPacket rp : resultPackets) {
            pLength = pLength + UUIDUtil.toBytes(rp.getResultUUID()).length + 1;
            bb.put(UUIDUtil.toBytes(rp.getResultUUID()));
            bb.put(rp.getResult());
            long resultDate = rp.getResultDate();
            //byte[] resultDateBytes = new byte[Long.BYTES];
            //resultDateBytes[0] = (byte) (resultDate >>> 56);//
            //resultDateBytes[1] = (byte) (resultDate >>> 48);//
            //resultDateBytes[2] = (byte) (resultDate >>> 40);//
            //resultDateBytes[3] = (byte) (resultDate >>> 32);//
            //resultDateBytes[4] = (byte) (resultDate >>> 24);//
            //resultDateBytes[5] = (byte) (resultDate >>> 16);//
            //resultDateBytes[6] = (byte) (resultDate >>> 8);//
            //resultDateBytes[7] = (byte) (resultDate);//
            bb.putLong(resultDate);
        }
        //System.out.println("total contained packet length actual " + pLength);
        //System.out.println("header size (+data) " + bytes.length);
        int finalSize = bb.position();
        //System.out.println("Actual allocated size " + finalSize + " bytes");
        finalSize = finalSize - 3; // This is PAYLOAD LENGTH
        //System.out.println("Size put in packet " + finalSize);
        //System.out.println("Buffer before flip, pos: " + bb.position()
        //        + " lim " + bb.limit()
        //        + " cap " + bb.capacity());
        // now set the size in the buffer before flipping
        bb.array()[1] = (byte) ((finalSize >> 8) & 0xFF);
        bb.array()[2] = (byte) (finalSize & 0xFF);
        // and flip
        bb.flip();
        //System.out.println("Buffer after flip, pos: " + bb.position()
        //        + " lim " + bb.limit()
        //        + " cap " + bb.capacity());
        ByteBuffer out = ByteBuffer.allocate(finalSize + 3);
        System.arraycopy(bb.array(), 0, out.array(), 0, (finalSize + 3));
        //System.out.println("Out after allocate, pos: " + out.position()
        //        + " lim " + out.limit()
        //        + " cap " + out.capacity());
        return out;
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_SEND_RESULTS;
    }

    @Override
    public boolean hasResults() {
        return (!resultPackets.isEmpty());
    }

    /**
     *
     * @param resultPacket
     * @return False if you are trying to add more than 255 dates, true
     * otherwise
     */
    @Override
    public boolean addResult(ResultPacket resultPacket) {
        return (resultPackets.size() > 254) ? false : resultPackets.add(resultPacket);
    }

    @Override
    public ArrayDeque<ResultPacket> getResults() {
        return this.resultPackets;
    }

    @Override
    public void clearResults() {
        resultPackets.clear();
    }

    public static void main(String[] args) {
        SendResultsPacket nup = new SendResultsPacket(null, null);

        for (int i = 0; i < 10; i++) {
            long resultDate = System.currentTimeMillis();
            UUID uuid = UUID.randomUUID();
            byte type = SendResultsPacketIF.RES_POSITIVE;
            nup.addResult(new ResultPacket(uuid, type, resultDate));
            //nup.addResult(new ResultPacket(uuid, type));
            System.out.println("Constructed ResultPacket: UUID " + uuid + " result type " + type + " result date " + resultDate);
        }

        SendResultsPacket nup2 = new SendResultsPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);
        ArrayDeque<ResultPacket> results = nup2.getResults();
        int cnt = 0;
        for (ResultPacket rp : results) {
            System.out.println("Reconstructed ResultPacket: UUID " + rp.getResultUUID() + " result type " + rp.getResult() + " result date " + rp.getResultDate());
            cnt++;
        }

        System.out.println("CNT " + cnt);
        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
    }

}
