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

import java.util.UUID;
import com.eggze.spreadit.common.packet.client.ClientPacketIF;

/**
 * An interface describing the client packet where a user sends a scan of a
 * Location-QR. Upon correct reception and successful insertion of the scan in
 * the database by the ScanManager, a ScanOKPacket is returned.
 * 
 * If the scan already exists in the database, or any other error occurs, a
 * ServerErrorPacket is returned. The server error packet describing a
 * SCAN_EXISTS error allows clients that never received a ScanOKPacket to verify
 * that the scan they attempted to send has already been recorded.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface UserScanPacketIF extends ClientPacketIF{
   
    /**
     * Set the location UUID associated with this packet.
     * 
     * @param locUUID The locUUID associated with this packet
     */
    public void setLocUUID(UUID locUUID);
    
    /**
     * Set the location UUID associated with this packet as a byte array.
     * 
     * @param locUUID The locUUID associated with this packet as a byte array
     */
    public void setLocUUID(byte[] locUUID);
    
    /**
     * Get the location UUID associated with this packet as a byte array.
     * 
     * @return The locUUID associated with this packet as a byte array
     */
    public byte[] getLocUUIDBytes();
    
    /**
     * Get the location UUID associated with this packet.
     * 
     * @return The locUUID associated with this packet
     */
    public UUID getLocUUID();
    
    /**
     * Set the scan UUID associated with this packet.
     * 
     * @param scanUUID The scanUUID associated with this packet
     */
    public void setScanUUID(UUID scanUUID);
    
    /**
     * Set the scan UUID associated with this packet as a byte array.
     * 
     * @param scanUUID The scanUUID associated with this packet as a byte array
     */
    public void setScanUUID(byte[] scanUUID);
    
    /**
     * Get the scan UUID associated with this packet as a byte array.
     * 
     * @return The scanUUID associated with this packet as a byte array
     */
    public byte[] getScanUUIDBytes();
    
    /**
     * Get the scan UUID associated with this packet.
     * 
     * @return The scanUUID associated with this packet
     */
    public UUID getScanUUID();
    
    /**
     * Get the timestamp of when a Location-QR was scanned by a user.
     * 
     * @return the timestamp of when a Location-QR was scanned by a user
     */
    public long getScanTimestamp();
    
    /**
     * Set the timestamp of when a Location-QR was scanned by a user.
     * 
     * @param scanTimestamp the timestamp of when a Location-QR was scanned by a user
     */
    public void setScanTimestamp(long scanTimestamp);

    @Deprecated
    public byte getScanTraceStatus();
    @Deprecated
    public void setScanTraceStatus(byte traceStatus);

}
