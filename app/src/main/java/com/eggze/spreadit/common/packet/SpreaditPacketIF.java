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
package com.eggze.spreadit.common.packet;

import ch.dermitza.securenio.packet.PacketIF;
import java.nio.charset.Charset;

/**
 * The top level interface describing all packets of the spreadit system. All
 * packets contain a protocol version to be used to verify correct communication
 * and a packet index that is used to correlate packets with their responses, be
 * it a correct or an error response.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface SpreaditPacketIF extends PacketIF {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final byte PROTOCOL_VERSION = 0x00; // Communication protocol version

    public static final byte TYPE_SERVER = (byte) 0xF5; // server packets
    public static final byte TYPE_CLIENT = (byte) 0x4F; // client packets
    public static final byte TYPE_LOCATION = (byte) 0xF4; // location provider packets
    public static final byte TYPE_RESULT = (byte) 0xF2; // result provider packets
    public static final byte TYPE_AUDIT = (byte) 0xF0; // audit manager packets
    public static final byte TYPE_INVALID = Byte.MAX_VALUE;
    
    /**
     * Set the packet index of this packet. The packet index is used to keep
     * track of a packet between multiple packet transmissions.
     * 
     * @param packetIndex the packet index of this packet
     */
    public void setPacketIndex(short packetIndex);
    
    /**
     * Get the packet index of this packet. The packet index is used to keep
     * track of a packet between multiple packet transmissions.
     * 
     * @return the packet index of this packet
     */
    public short getPacketIndex();
    
    /**
     * Set the protocol version of this packet. The protocol version is used to
     * ensure all communications are using the same protocol.
     * 
     * @param protocolVersion the protocol version of this packet
     */
    public void setProtocolVersion(byte protocolVersion);
    
    /**
     * Get the protocol version of this packet. The protocol version is used to
     * ensure all communications are using the same protocol.
     * 
     * @return the protocol version of this packet
     */
    public byte getProtocolVersion();

}
