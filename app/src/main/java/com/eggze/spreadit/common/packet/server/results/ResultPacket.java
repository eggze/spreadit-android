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

import java.util.UUID;

/**
 * The ResultPacket contains the resultUUID together with the result, if the
 * result is *not* RES_PENDING or RES_INVALID. It is sent from the
 * ResultsManager via a SendResultsPacket.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public final class ResultPacket {
    
    private final UUID resultUUID;
    private final byte result;
    private final long resultDate;

    public ResultPacket(UUID resultUUID, byte result, long resultDate) {
        this.resultUUID = resultUUID;
        this.result = result;
        this.resultDate = resultDate;
    }

    /**
     * Get the resultUUID of this ResultPacket
     * @return the resultUUID of this ResultPacket
     */
    public UUID getResultUUID() {
        return this.resultUUID;
    }

    /**
     * Get the test result of this ResultPacket
     * @return the test result of this ResultPacket
     */
    public byte getResult() {
        return this.result;
    }

    /**
     * Get the date the result were returned (or the test was made) of this
     * ResultPacket.
     * 
     * @return the date the result were returned (or the test was made) of this
     * ResultPacket
     * 
     */
    public long getResultDate() {
        return this.resultDate;
    }

}
