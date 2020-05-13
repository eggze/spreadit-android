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
package com.eggze.spreadit.common.packet.client.results;

import java.util.UUID;
import com.eggze.spreadit.common.packet.client.ClientPacketIF;

/**
 * An interface describing the client packet where a user sends a scan of a
 * Test-QR to the spreadit backend. Reception of the scan is acknowledged via a
 * reply of a ResultOKPacket from the ResultManager, or a ServerErrorPacket.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface UserResultPacketIF extends ClientPacketIF{
    
    /**
     * Set the result UUID associated with this packet.
     * 
     * @param resultUUID The resultUUID associated with this packet
     */
    public void setResultUUID(UUID resultUUID);
    
    /**
     * Set the result UUID associated with this packet as a byte array.
     * 
     * @param resultUUID The resultUUID associated with this packet as a byte array
     */
    public void setResultUUID(byte[] resultUUID);
    
    /**
     * Get the result UUID associated with this packet as a byte array.
     * 
     * @return The resultUUID associated with this packet as a byte array
     */
    public byte[] getResultUUIDBytes();
    
    /**
     * Get the result UUID associated with this packet.
     * 
     * @return The resultUUID associated with this packet
     */
    public UUID getResultUUID();

    /**
     * Get the timestamp of when the test was performed. Deprecated.
     *
     * @return the timestamp of when the test was performed
     */
    @Deprecated
    public long getResultTimestamp();

    /**
     * Set the timestamp of when the test scanned was performed. Deprecated.
     *
     * @param resultTimestamp the timestamp of when the test scanned was performed
     */
    @Deprecated
    public void setResultTimestamp(long resultTimestamp);

}
