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

import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import java.util.ArrayDeque;

/**
 * An interface describing the server packet where the ResultsManager returns
 * results that have had their results set via the TestProvider. In the event
 * the client has no results set yet, an empty SendResultsPacket is sent. On any
 * other error, a ServerErrorPacket is returned instead.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface SendResultsPacketIF extends ServerPacketIF{
    
    public static final byte RES_PENDING = 0x00;
    public static final byte RES_NEGATIVE = 0x01;
    public static final byte RES_RETEST = 0x02;
    public static final byte RES_POSITIVE = 0x03;
    public static final byte RES_INVALID = Byte.MAX_VALUE; // max value is invalid (user does not exist)

    public static final String[] RES_NAMES = {
        "RES_PENDING",
        "RES_NEGATIVE",
        "RES_RETEST",
        "RES_POSITIVE",
        "RES_INVALID"};
    
    /**
     * Whether this SendResultsPacket contains any ResultPackets
     * 
     * @return true if this SendResultsPacket contains ResultPackets, false
     * otherwise
     */
    abstract boolean hasResults();
    
    /**
     * Add a ResultPacket to this SendResultsPacket.
     * 
     * @param result the ResultPacket to add
     * @return true if the result was added, false otherwise. The number of
     * ResultPackets to be added is limited to 254 results per SendResultsPacket
     * 
     */
    abstract boolean addResult(ResultPacket result);
    
    /**
     * Get the ArrayDeque containing the ResultPackets.
     *
     * @return the ArrayDeque containing the ResultPackets
     */
    abstract public ArrayDeque<ResultPacket> getResults();
    
    /**
     * Clear the ArrayDeque of any ResultPackets in this SendResultsPacket.
     * After calling this method, no ResultPackets will be contained within
     * this SendResultsPacket.
     */
    abstract public void clearResults();

}
