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
package com.eggze.spreadit.common.packet.server.user;

import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import java.util.UUID;

/**
 * An interface describing the server packet where the UserManager responds to
 * a UserHelloPacket. This packet also contains the total number of contacts
 * the user had over all his scans in the contact tracing time window (15 days)
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface ServerHelloPacketIF extends ServerPacketIF{
    
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
    
    /**
     * Get the number of contacts this user has across all their scans.
     * 
     * @return the number of contacts this user has across all their scans
     */
    public short getContacts();
    
    /**
     * Set the number of contacts this user has across all their scans.
     * 
     * @param contacts the number of contacts this user has across all their
     * scans
     * 
     */
    public void setContacts(short contacts);
    
    @Deprecated
    public byte getTestResult();
    @Deprecated
    public void setTestResult(byte result);

}
