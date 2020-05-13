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
package com.eggze.spreadit.common.packet.server.impl;

import com.eggze.spreadit.common.packet.server.ServerPacketIF;

/**
 * This interface describes a ServerErrorPacket. This is a generic packet that
 * incorporates multiple errors from all entities. The error packets carry a
 * packet index that is used primarily by the client to map the error received
 * to a request it made at some point in time.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface ServerErrorPacketIF extends ServerPacketIF{
    
    // TODO randomise these
    // Generic error descriptions to clients
    // more specific errors belong elsewhere
    
    public static final String[] E_NAMES = {
                                                "No Error",
                                                "Server Error",
                                                "User Exists",
                                                "Invalid User",
                                                "Invalid Location",
                                                "Unknown Result ID",
                                                "Scan exists",
                                                "No contacts",
                                                "No results",
                                                "Wrong comms protocol",
                                                "Unknown Error",
                                                "Invalid Packet Index",
                                                "Invalid Error Type"};
    
    public static final short E_OK = 0; // NEEDED TO VALIDATE IT IS NOT AN ERROR
    public static final short E_SERVER_ERROR = 1;
    public static final short E_USER_EXISTS = 2; // During new registration - user already exists in user db
    public static final short E_USER_INVALID = 3;
    public static final short E_LOC_INVALID = 4;
    public static final short E_RESULT_INVALID = 5; // Result trying to be scanned is already entered in results db
    public static final short E_SCAN_INVALID = 6;
    public static final short E_NO_CONTACTS = 7; // User has not been flagged as being suspected
    public static final short E_NO_RESULTS = 8; // User has not been flagged as having test results
    public static final short E_WRONG_PROTOCOL = 9;
    public static final short E_UNKNOWN_ERROR = 10;
    public static final short E_INVALID_PACKET_IDX = 11;
    public static final short E_INVALID = Short.MAX_VALUE;
    
    /**
     * Set the error ID of this ServerErrorPacket.
     * @param errorID the error ID of this ServerErrorPacket
     */
    public void setErrorID(short errorID);
    
    /**
     * Get the error ID of this ServerErrorPacket.
     * @return the error ID of this ServerErrorPacket
     */
    public short getErrorID();
    
    /**
     * Get the client packet type this error is in response of.
     * @return the client packet type this error is in response of
     */
    public short getClientPacketType();
    
    /**
     * Set the client packet type this error is in response of.
     * @param clientPacketType the client packet type this error is in response of
     */
    public void setClientPacketType(short clientPacketType);
    
}
