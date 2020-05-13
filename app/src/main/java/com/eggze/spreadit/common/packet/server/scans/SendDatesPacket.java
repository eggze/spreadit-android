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
import com.eggze.spreadit.common.util.ByteUtil;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class SendDatesPacket extends ServerPacket implements SendDatesPacketIF {

    private ArrayDeque<Long> dates;
    private long requestedTimestamp =0;

    public SendDatesPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
        dates = new ArrayDeque<>(10); // Initial capacity 10
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        //System.out.println("Source before get, pos: " + source.position()
        //        + " lim " + source.limit()
        //        + " cap " + source.capacity());
        // First byte is type, ignore
        source.get();
        this.protocolVersion = source.get();
        this.packetIndex =  (short)(((source.get() & 0xFF) << 8)//
                            | ((source.get() & 0xFF)));//
        requestedTimestamp = ((source.get() & 0xFFL) << 56)//
                | ((source.get() & 0xFFL) << 48)//
                | ((source.get() & 0xFFL) << 40)//
                | ((source.get() & 0xFFL) << 32)//
                | ((source.get() & 0xFFL) << 24)//
                | ((source.get() & 0xFFL) << 16)//
                | ((source.get() & 0xFFL) << 8)//
                | ((source.get() & 0xFFL));//

        int packets = 0;
        try{
            packets = ((source.get() & 0xFF));
        } catch (BufferUnderflowException bue){
            // no packets
            return;
        }
        if(packets == 0){
            // Nothing to add
            return;
        }
        for(int i=0; i < packets; i++){
            byte[] tmp = new byte[Long.BYTES];
            source.get(tmp);
            dates.add(ByteUtil.bytesToLong(tmp));
        }
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1 // type
                + 1 // protocol version
                + 2 // packet index
                + 8  // last insertion timestamp
                + 1; // number of contained packets (1 byte)
        
        int packetCount = dates.size();
        bytes = new byte[len + 3]; // + size of header
        bytes[0] = SpreaditPacket.TYPE_SERVER;
        // Dont assign the length yet as we dont know it
        bytes[3] = ServerPacketIF.T_SEND_DATES;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        
        bytes[7] = (byte) (requestedTimestamp >>> 56);//
        bytes[8] = (byte) (requestedTimestamp >>> 48);//
        bytes[9] = (byte) (requestedTimestamp >>> 40);//
        bytes[10] = (byte) (requestedTimestamp >>> 32);//
        bytes[11] = (byte) (requestedTimestamp >>> 24);//
        bytes[12] = (byte) (requestedTimestamp >>> 16);//
        bytes[13] = (byte) (requestedTimestamp >>> 8);//
        bytes[14] = (byte) (requestedTimestamp);//
        
        bytes[15] = (byte) (packetCount & 0xFF);
        
        if (packetCount == 0) {
            // No feelings, assign the lenth
            bytes[1] = (byte) ((len >> 8) & 0xFF);
            bytes[2] = (byte) (len & 0xFF);
            // And return the packet
            return ByteBuffer.wrap(bytes);
        }
        // Otherwise lets do the size manually
        int maxTotalSize = bytes.length + Long.BYTES * packetCount;
        //System.out.println("Max size allocated " + maxTotalSize + " bytes");
        // Allocate a bytebuffer big enough
        ByteBuffer bb = ByteBuffer.allocate(maxTotalSize);
        // add the header
        bb.put(bytes);
        int pLength = 0;
        // add all the data from the packets
        for (Long l : dates) {
            pLength = pLength + ByteUtil.longToBytes(l).length;
            bb.put(ByteUtil.longToBytes(l));
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
        ByteBuffer out = ByteBuffer.allocate(finalSize+3);
        System.arraycopy(bb.array(), 0, out.array(), 0, (finalSize+3));
        //System.out.println("Out after allocate, pos: " + out.position()
        //        + " lim " + out.limit()
        //        + " cap " + out.capacity());
        return out;
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_SEND_DATES;
    }

    @Override
    public boolean hasDates() {
        return (!dates.isEmpty());
    }

    /**
     * 
     * @param date
     * @return False if you are trying to add more than 255 dates, true otherwise
     */
    @Override
    public boolean addDate(long date) {
        return (dates.size() > 254) ? false : dates.add(date);
    }

    @Override
    public ArrayDeque<Long> getDates() {
        return this.dates;
    }

    @Override
    public void clearDates() {
        dates.clear();
    }

    public static void main(String[] args) {
        SendDatesPacket nup = new SendDatesPacket(null, null);
        long d1 = System.currentTimeMillis();
        long d2 = System.currentTimeMillis();
        long d3 = System.currentTimeMillis();
        //System.out.println("Adding: " + d1 + " " + d2 + " " + d3);
        //nup.addDate(System.currentTimeMillis());
        //nup.addDate(System.currentTimeMillis());
        //nup.addDate(System.currentTimeMillis());
        for(int i=0; i < 333; i++){
            nup.addDate(System.currentTimeMillis());
        }
        

        SendDatesPacket nup2 = new SendDatesPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);
        ArrayDeque<Long> dates = nup2.getDates();
        int cnt = 0;
        for(Long l : dates){
            System.out.println("Date: " + l);
            cnt++;
        }
        
        System.out.println("CNT " +cnt);
        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
    }

    @Override
    public long getRequestedTimestamp() {
        return this.requestedTimestamp;
    }

    @Override
    public void setRequestedTimestamp(long requestedTimestamp) {
        this.requestedTimestamp = requestedTimestamp;
    }

}
