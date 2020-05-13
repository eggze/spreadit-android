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
package com.eggze.spreadit.common.util.scan;

import java.util.UUID;

/**
 * A LocationScan class implementation, consisting of the base scanUUID together
 * with the latitude and longitude of the location itself.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class LocationScan extends AbstractClientScan{
    
    private final float lat;
    private final float lon;
    
    /**
     * Create a new LocationScan with latitude and longitude.
     * 
     * @param scanUUID The scanUUID of this LocationScan
     * @param lat The latitude of this LocationScan
     * @param lon The longitude of this LocationScan
     */
    public LocationScan(UUID scanUUID, float lat, float lon){
        super(scanUUID);
        this.lat = lat;
        this.lon = lon;
    }
    
    /**
     * Create a new LocationScan. Latitude and longitude are set to 0 when using
     * this constructor.
     * 
     * @param scanUUID The scanUUID of this LocationScan
     */
    public LocationScan(UUID scanUUID){
        super(scanUUID);
        this.lat = 0;
        this.lon = 0;
    }
    
    /**
     * Return the latitude of this LocationScan. If the LocationScan has been
     * initialized only with a scanUUID, its latitude is 0.
     * 
     * @return the latitude of this LocationScan
     */
    public float getLat(){
        return this.lat;
    }
    
    /**
     * Return the longitude of this LocationScan. If the LocationScan has been
     * initialized only with a scanUUID/locUUID, its longitude is 0.
     * 
     * @return the longitude of this LocationScan
     */
    public float getLon(){
        return this.lon;
    }

}
