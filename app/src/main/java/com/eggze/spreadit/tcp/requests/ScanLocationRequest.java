/**
 * This file is part of spreadit-android. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.tcp.requests;

import com.eggze.spreadit.common.packet.client.scans.UserScanPacket;
import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.tcp.ResponseListener;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class ScanLocationRequest extends Request {

    public ScanLocationRequest(User user, ScanLocation scanLocation, ResponseListener responseListener) {
        super(responseListener);
        UserScanPacket usp = new UserScanPacket(networkManager.getClient(), networkManager.getClient().getSocket());
        usp.setPacketIndex((short) scanLocation.getId());
        usp.setUserUUID(user.getUuid());
        usp.setScanUUID(scanLocation.getUuid());
        usp.setLocUUID(scanLocation.getUuid());
        usp.setScanTimestamp(scanLocation.getScanTimestamp());
        setRequestPacket(usp);
    }
}
