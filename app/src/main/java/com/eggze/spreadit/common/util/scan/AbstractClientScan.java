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

import java.util.Objects;
import java.util.UUID;

/**
 * An abstract ClientScan class that serves as the base for all QR scans used
 * in the spreadit system.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public abstract class AbstractClientScan implements ClientScanIF{

    // The final scanUUID contained in this object
    protected final UUID scanUUID;
    
    // Disallow blank instantiation
    private AbstractClientScan(){scanUUID = null;}
    
    /**
     * Create a new AbstractClientScan object. 
     * 
     * @param scanUUID The final scanUUID this object contains
     */
    public AbstractClientScan(UUID scanUUID){
        this.scanUUID = scanUUID;
    }
    
    @Override
    public UUID getScanUUID() {
        return this.scanUUID;
    }
    
    /**
     * A custom equals() implementation that uses the contained scanUUID to
     * check for object equality. As the comparison is based on the contained
     * UUID objects, this function is essentially a wrapper for checking
     * UUID equality.
     * 
     * @param o The Object to compare equality against
     * @return True if this object equals the provided one, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        if (this.getClass().equals(o.getClass())) {
            return ((AbstractClientScan)o).getScanUUID().equals(this.scanUUID);
        }
        return false;
    }

    /**
     * A custom hashCode() implementation that uses the contained scanUUID to
     * create a hash for this object.
     * 
     * @return A hashcode based on the contained scanUUID
     */
    @Override
    public int hashCode() {
        int hash = 7;
        // TODO, work around hashCode() requiring API 19, possibly custom implementation
        hash = 47 * hash + Objects.hashCode(this.scanUUID);
        return hash;
    }

}
