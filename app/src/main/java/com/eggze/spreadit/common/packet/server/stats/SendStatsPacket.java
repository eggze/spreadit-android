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
package com.eggze.spreadit.common.packet.server.stats;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import java.nio.ByteBuffer;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class SendStatsPacket extends ServerPacket implements SendStatsPacketIF {
    
    private long users;
    private long scans;
    private long locations;
    private long tests;
    private long testedUsers;
    private long positives;
    private long contacts;
    private long statsTimestamp;

    public SendStatsPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
    }

    @Override
    public void reconstruct(ByteBuffer source) {
        //byte[] bytes = source.array();
        source.get();// byte[0] is type, ignore
        this.protocolVersion = source.get();
        this.packetIndex = source.getShort();
        this.users = source.getLong();
        this.scans = source.getLong();
        this.locations = source.getLong();
        this.tests = source.getLong();
        this.testedUsers = source.getLong();
        this.positives = source.getLong();
        this.contacts = source.getLong();
        this.statsTimestamp = source.getLong();
        //this.protocolVersion = bytes[1];
        //this.packetIndex =  (short)(((bytes[2] & 0xFF) << 8)//
        //                    | ((bytes[3] & 0xFF)));//
        //this.users = ((bytes[4] & 0xFF) << 24)//
        //        | ((bytes[5] & 0xFF) << 16)//
        //        | ((bytes[6] & 0xFF) << 8)//
        //        | ((bytes[7] & 0xFF));//
        //this.scans = ((bytes[8] & 0xFF) << 24)//
        //        | ((bytes[9] & 0xFF) << 16)//
        //        | ((bytes[10] & 0xFF) << 8)//
        //        | ((bytes[11] & 0xFF));//
        //this.locations = ((bytes[12] & 0xFF) << 24)//
        //        | ((bytes[13] & 0xFF) << 16)//
        //        | ((bytes[14] & 0xFF) << 8)//
        //        | ((bytes[15] & 0xFF));//
        //this.tested = ((bytes[16] & 0xFF) << 24)//
        //        | ((bytes[17] & 0xFF) << 16)//
        //        | ((bytes[18] & 0xFF) << 8)//
        //        | ((bytes[19] & 0xFF));//
        //this.infected = ((bytes[20] & 0xFF) << 24)//
        //        | ((bytes[21] & 0xFF) << 16)//
        //        | ((bytes[22] & 0xFF) << 8)//
        //        | ((bytes[23] & 0xFF));//
        //this.suspected = ((bytes[24] & 0xFF) << 24)//
        //        | ((bytes[25] & 0xFF) << 16)//
        //        | ((bytes[26] & 0xFF) << 8)//
        //        | ((bytes[27] & 0xFF));//
        //statsTimestamp = ((bytes[28] & 0xFFL) << 56)//
        //        | ((bytes[29] & 0xFFL) << 48)//
        //        | ((bytes[30] & 0xFFL) << 40)//
        //        | ((bytes[31] & 0xFFL) << 32)//
        //        | ((bytes[32] & 0xFFL) << 24)//
        //        | ((bytes[33] & 0xFFL) << 16)//
        //        | ((bytes[34] & 0xFFL) << 8)//
        //        | ((bytes[35] & 0xFFL));//
        
        //System.out.println("RECONSTRUCT Length: " + bytes.length 
        //        + " Type: " + bytes[0]
        //        + " Users: " + this.users
        //        + " Scans: " + this.scans
        //        + " Locations: " + this.locations
        //        + " Tested: " + this.tested
        //        + " Infected: " + this.infected
        //        + " Suspected: " + this.suspected
        //        + " Timestamp: " + this.statsTimestamp);
    }

    @Override
    public ByteBuffer toBytes() {
        byte[] bytes;
        int len = 1  // type
                + 1 // protocol version
                + 2 // packet index
                + 8 // users
                + 8 // scans
                + 8 // locations
                + 8 // tests
                + 8 // tested
                + 8 // infected
                + 8 // suspected
                + 8; // timestamp;

        bytes = new byte[len + 3]; // Including header + message length
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.put(SpreaditPacket.TYPE_SERVER);
        bb.putShort((short)len);
        bb.put(ServerPacketIF.T_SEND_STATS);
        bb.put(this.protocolVersion);
        bb.putShort(this.packetIndex);
        bb.putLong(users);
        bb.putLong(scans);
        bb.putLong(locations);
        bb.putLong(tests);
        bb.putLong(testedUsers);
        bb.putLong(positives);
        bb.putLong(contacts);
        bb.putLong(statsTimestamp);
        bb.rewind();
        /*
        bytes[0] = SpreaditPacket.TYPE_SERVER;
        bytes[1] = (byte) ((len >> 8) & 0xFF);
        bytes[2] = (byte) (len & 0xFF);
        bytes[3] = ServerPacketIF.T_SEND_STATS;
        bytes[4] = this.protocolVersion;
        bytes[5] = (byte) (this.packetIndex >>> 8);//
        bytes[6] = (byte) (this.packetIndex);
        bytes[7] = (byte) (users >>> 56);//
        bytes[8] = (byte) (users >>> 48);//
        bytes[9] = (byte) (users >>> 40);//
        bytes[10] = (byte) (users >>> 32);//
        bytes[11] = (byte) (users >>> 24);//
        bytes[12] = (byte) (users >>> 16);//
        bytes[13] = (byte) (users >>> 8);//
        bytes[14] = (byte) (users);
        bytes[15] = (byte) (scans >>> 56);//
        bytes[16] = (byte) (scans >>> 48);//
        bytes[17] = (byte) (scans >>> 40);//
        bytes[18] = (byte) (scans >>> 32);//
        bytes[19] = (byte) (scans >>> 24);//
        bytes[20] = (byte) (scans >>> 16);//
        bytes[21] = (byte) (scans >>> 8);//
        bytes[22] = (byte) (scans);
        bytes[23] = (byte) (locations >>> 56);//
        bytes[24] = (byte) (locations >>> 48);//
        bytes[25] = (byte) (locations >>> 40);//
        bytes[26] = (byte) (locations >>> 32);//
        bytes[27] = (byte) (locations >>> 24);//
        bytes[28] = (byte) (locations >>> 16);//
        bytes[29] = (byte) (locations >>> 8);//
        bytes[30] = (byte) (locations);
        bytes[31] = (byte) (tests >>> 56);//
        bytes[32] = (byte) (tests >>> 48);//
        bytes[33] = (byte) (tests >>> 40);//
        bytes[34] = (byte) (tests >>> 32);//
        bytes[35] = (byte) (tests >>> 24);//
        bytes[36] = (byte) (tests >>> 16);//
        bytes[37] = (byte) (tests >>> 8);//
        bytes[38] = (byte) (tests);
        bytes[39] = (byte) (tested >>> 56);//
        bytes[40] = (byte) (tested >>> 48);//
        bytes[41] = (byte) (tested >>> 40);//
        bytes[42] = (byte) (tested >>> 32);//
        bytes[43] = (byte) (tested >>> 24);//
        bytes[44] = (byte) (tested >>> 16);//
        bytes[45] = (byte) (tested >>> 8);//
        bytes[46] = (byte) (tested);
        bytes[47] = (byte) (positives >>> 56);//
        bytes[48] = (byte) (positives >>> 48);//
        bytes[49] = (byte) (positives >>> 40);//
        bytes[50] = (byte) (positives >>> 32);//
        bytes[51] = (byte) (positives >>> 24);//
        bytes[52] = (byte) (positives >>> 16);//
        bytes[53] = (byte) (positives >>> 8);//
        bytes[54] = (byte) (positives);
        bytes[55] = (byte) (suspected >>> 56);//
        bytes[56] = (byte) (suspected >>> 48);//
        bytes[57] = (byte) (suspected >>> 40);//
        bytes[58] = (byte) (suspected >>> 32);//
        bytes[59] = (byte) (suspected >>> 24);//
        bytes[60] = (byte) (suspected >>> 16);//
        bytes[61] = (byte) (suspected >>> 8);//
        bytes[62] = (byte) (suspected);
        bytes[63] = (byte) (statsTimestamp >>> 56);//
        bytes[64] = (byte) (statsTimestamp >>> 48);//
        bytes[65] = (byte) (statsTimestamp >>> 40);//
        bytes[66] = (byte) (statsTimestamp >>> 32);//
        bytes[67] = (byte) (statsTimestamp >>> 24);//
        bytes[68] = (byte) (statsTimestamp >>> 16);//
        bytes[69] = (byte) (statsTimestamp >>> 8);//
        bytes[70] = (byte) (statsTimestamp);//
        return ByteBuffer.wrap(bytes);
        */
        return bb;
    }
    
    @Override
    public long getUsers(){
        return this.users;
    }
    
    @Override
    public void setUsers(long users){
        this.users = users;
    }
    
    @Override
    public long getLocations(){
        return this.locations;
    }
    
    @Override
    public void setLocations(long locations){
        this.locations = locations;
    }
    
    @Override
    public long getScans(){
        return this.scans;
    }
    
    @Override
    public void setScans(long scans){
        this.scans = scans;
    }
    
    @Override
    public long getTestedUsers(){
        return this.testedUsers;
    }
    
    @Override
    public void setTestedUsers(long testedUsers){
        this.testedUsers = testedUsers;
    }
    
    @Override
    public long getTests(){
        return this.tests;
    }
    
    @Override
    public void setTests(long tests){
        this.tests = tests;
    }
    
    @Override
    public long getPositives(){
        return this.positives;
    }
    
    @Override
    public void setPositives(long positives){
        this.positives = positives;
    }
    
    @Override
    public long getContacts(){
        return this.contacts;
    }
    
    @Override
    public void setContacts(long contacts){
        this.contacts = contacts;
    }

    @Override
    public byte getServerPacketType() {
        return ServerPacketIF.T_SEND_STATS;
    }

    @Override
    public long getStatsTimestamp() {
        return this.statsTimestamp;
    }

    @Override
    public void setStatsTimestamp(long statsTimestamp) {
        this.statsTimestamp = statsTimestamp;
    }
    
    public static void main(String[] args) {
        SendStatsPacket nup = new SendStatsPacket(null, null);
        nup.setPacketIndex((byte)224);
        nup.setUsers(10008876);
        nup.setLocations(104560);
        nup.setScans(5003450);
        nup.setTests(203460);
        nup.setPositives(234660);
        nup.setContacts(2533630);
        nup.setStatsTimestamp(System.currentTimeMillis());
        System.out.println("Set Type: " + nup.getServerPacketType()
                            + " Index: " + nup.getPacketIndex()
                            + " Users: " + nup.getUsers()
                            + " Locations: " + nup.getLocations()
                            + " Scans: " + nup.getScans()
                            + " Tested: " + nup.getTests()
                            + " Infected: " + nup.getPositives()
                            + " Suspected: " + nup.getContacts()
                            + " Timestamp: " + nup.getStatsTimestamp());

        SendStatsPacket nup2 = new SendStatsPacket(null, null);
        nup2.reconstructPadded(nup.toBytes(), false);
        System.out.println("Recunstructed Type: " + nup2.getServerPacketType()
                            + " Users: " + nup2.getUsers()
                            + " Index: " + nup2.getPacketIndex()
                            + " Locations: " + nup2.getLocations()
                            + " Scans: " + nup2.getScans()
                            + " Tested: " + nup2.getTests()
                            + " Infected: " + nup2.getPositives()
                            + " Suspected: " + nup2.getContacts()
                            + " Timestamp: " + nup2.getStatsTimestamp());
    }

}
