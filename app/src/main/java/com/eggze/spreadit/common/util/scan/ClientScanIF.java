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
 * A base Interface implementation for AbstractClientScan objects. Each
 * AbstractClientScan object contains at minimum a ScanUUID.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * 
 * @version 0.1
 * @since 0.1
 */
public interface ClientScanIF {
   
    /**
     * Returns the scanUUID contained by this ClientScanIF.
     * 
     * @return the scanUUID contained by this ClientScanIF.
     */
    public UUID getScanUUID();
}
