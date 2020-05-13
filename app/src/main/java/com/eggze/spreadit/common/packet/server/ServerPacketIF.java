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
package com.eggze.spreadit.common.packet.server;

/**
 * The base ServerPacketIF describing all ServerPackets used in the spreadit
 * implementation. All server packets interfaces extend this.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface ServerPacketIF {
    
    // User types
    public static final byte T_USER_CREATED = 0x00;
    public static final byte T_SERVER_HELLO = 0x01;
    // Scan types
    public static final byte T_SCAN_OK = 0x02;
    public static final byte T_SEND_DATES = 0x03;
    public static final byte T_DATES_RECEIVED_OK = 0x04;
    // Suspected types
    public static final byte SUS_INVALID = Byte.MAX_VALUE; // max value is invalid (user does not exist)
    // Result types
    public static final byte T_RESULT_OK = 0x05;
    public static final byte T_SEND_RESULTS = 0x06;
    // Stats types
    public static final byte T_SEND_STATS = 0x07;
    // Server types
    public static final byte T_SERVER_INFO = 0x08;
    public static final byte T_SERVER_ERROR = 0x09;
    public static final byte T_SERVER_INTERNAL_ERROR = 0x10;
    public static final byte T_SERVER_INVALID = Byte.MAX_VALUE;  // max value is invalid
    
    public static final String[] T_NAMES = {
                                                "T_USER_CREATED",
                                                "T_SERVER_HELLO",
                                                "T_SCAN_OK",
                                                "T_SEND_DATES",
                                                "T_DATES_RECEIVED_OK",
                                                "SUS_INVALID",
                                                "T_RESULT_OK",
                                                "T_SEND_RESULTS",
                                                "T_SEND_STATS",
                                                "T_SERVER_INFO",
                                                "T_SERVER_ERROR",
                                                "T_SERVER_INTERNAL_ERROR",
                                                "T_SERVER_INVALID"};
    
    /**
     * Get the packet type of this server packet.
     * 
     * @return the packet type of this server packet
     */
    public byte getServerPacketType();
}
