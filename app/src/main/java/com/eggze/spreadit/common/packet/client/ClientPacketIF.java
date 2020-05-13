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
package com.eggze.spreadit.common.packet.client;

import java.util.UUID;

/**
 * The base ClientPacketIF describing all ClientPackets used for the spreadit
 * implementation. All client packet interfaces extend this.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface ClientPacketIF {
    
    // User types
    public static final byte T_NEW_USER = 0x00;
    public static final byte T_USER_HELLO = 0x01;
    
    // Scan types
    public static final byte T_USER_SCAN = 0x02;
    public static final byte T_REQ_DATES = 0x03;
    public static final byte T_DATES_RECEIVED = 0x04;
    
    // Results types
    public static final byte T_USER_RESULT = 0x05;
    public static final byte T_REQ_RESULTS = 0x06;
    
    // Info types
    public static final byte T_REQ_STATS = 0x07;
    
    // Invalid type
    public static final byte T_INVALID = Byte.MAX_VALUE;
    
    /**
     * Get the type of this client packet.
     * 
     * @return the type of this client packet
     */
    public byte getClientPacketType();

    /**
     * Set the user UUID associated with this packet.
     * 
     * @param userUUID The userUUID associated with this packet
     */
    public void setUserUUID(UUID userUUID);
    
    /**
     * Set the user UUID associated with this packet as a byte array.
     * 
     * @param userUUID The userUUID associated with this packet as a byte array
     */
    public void setUserUUID(byte[] userUUID);
    
    /**
     * Get the user UUID associated with this packet as a byte array.
     * 
     * @return The userUUID associated with this packet as a byte array
     */
    public byte[] getUserUUIDBytes();
    
    /**
     * Get the user UUID associated with this packet.
     * 
     * @return The userUUID associated with this packet
     */
    public UUID getUserUUID();
}
