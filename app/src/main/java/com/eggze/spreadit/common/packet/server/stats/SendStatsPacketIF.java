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

import com.eggze.spreadit.common.packet.server.ServerPacketIF;

/**
 * An interface describing the server packet where the StatsManager sends stats
 * of the spreadit system.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface SendStatsPacketIF extends ServerPacketIF{
    
    /**
     * Get the total number of users in the spreadit system.
     * 
     * @return the total number of users in the spreadit system
     */
    public long getUsers();
    
    /**
     * Set the total number of users in the spreadit system.
     * 
     * @param users the total number of users in the spreadit system
     */
    public void setUsers(long users);
    
    /**
     * Get the total number of Location-QR scans in the spreadit system.
     * 
     * @return the total number of Location-QR scans in the spreadit system
     */
    public long getScans();
    
    /**
     * Set the total number of Location-QR scans in the spreadit system.
     * 
     * @param scans the total number of Location-QR scans in the spreadit system
     */
    public void setScans(long scans);
    
    /**
     * Get the total number of Location-QRs in the spreadit system.
     * 
     * @return the total number of Location-QRs in the spreadit system
     */
    public long getLocations();
    
    /**
     * Set the total number of Location-QRs in the spreadit system.
     * 
     * @param locations the total number of Location-QRs in the spreadit system
     */
    public void setLocations(long locations);
    
    /**
     * Get the total number of users with tests in the spreadit system.
     * 
     * @return the total number of users with tests in the spreadit system
     */
    public long getTestedUsers();
    
    /**
     * Set the total number of users with tests in the spreadit system.
     * 
     * @param testedUsers the total number of users with tests in the spreadit
     * system
     * 
     */
    public void setTestedUsers(long testedUsers);
    
    /**
     * Get the total number of Test-QRs scanned in the spreadit system.
     * 
     * @return the total number of Test-QRs scanned in the spreadit system
     */
    public long getTests();
    
    /**
     * Set the total number of Test-QRs scanned in the spreadit system.
     * 
     * @param tests the total number of Test-QRs scanned in the spreadit system
     */
    public void setTests(long tests);
    
    /**
     * Get the total number of positive users in the spreadit system.
     * 
     * @return the total number of positive users in the spreadit system
     */
    public long getPositives();
    
    /**
     * Set the total number of positive users in the spreadit system.
     * 
     * @param positives the total number of positive users in the spreadit system
     */
    public void setPositives(long positives);
    
    /**
     * Set the total number of contacts across all scans in the spreadit system.
     * 
     * @return the total number of contacts across all scans in the spreadit
     * system
     * 
     */
    public long getContacts();
    
    /**
     * Get the total number of contacts across all scans in the spreadit system.
     * 
     * @param contacts the total number of contacts across all scans in the
     * spreadit system
     * 
     */
    public void setContacts(long contacts);
    
    /**
     * Get the timestamp of when these stats have been generated. This timestamp
     * is set by the StatsManager, during its periodic update task.
     * 
     * @return the timestamp of when these stats have been generated
     */
    public long getStatsTimestamp();
    
    /**
     * Set the timestamp of when these stats have been generated. This timestamp
     * is set by the StatsManager, during its periodic update task.
     * 
     * @param statsTimestamp the timestamp of when these stats have been
     * generated
     * 
     */
    public void setStatsTimestamp(long statsTimestamp);

}
