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
 * A ResultScan class implementation, consisting of the base scanUUID together
 * with the serial number of the result.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public final class ResultScan extends AbstractClientScan{
    
    private final int scanSerial;

    /**
     * Create a new ResultScan with a serial number.
     * 
     * @param scanUUID The scanUUID of this ResultScan
     * @param scanSerial The serial number of this ResultScan
     */
    public ResultScan(UUID scanUUID, int scanSerial) {
        super(scanUUID);
        this.scanSerial = scanSerial;
    }
    
    /**
     * Create a new ResultScan. The serial number is set to 0 when using this
     * constructor.
     * 
     * @param scanUUID The scanUUID of this ResultScan
     */
    public ResultScan(UUID scanUUID){
        super(scanUUID);
        this.scanSerial = 0;
    }

    /**
     * Return the serial number of this ResultScan.
     * 
     * @return the serial number of this ResultScan
     */
    public int getScanSerial(){
        return this.scanSerial;
    }

}
