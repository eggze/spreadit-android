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

import com.eggze.spreadit.common.packet.client.stats.ReqStatsPacket;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.tcp.ResponseListener;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class StatsRequest extends Request {

    public StatsRequest(User user, ResponseListener responseListener) {
        super(responseListener);
        ReqStatsPacket rsp = new ReqStatsPacket(networkManager.getClient(), networkManager.getClient().getSocket());
        rsp.setPacketIndex((short) 1);
        rsp.setUserUUID(user.getUuid());
        setRequestPacket(rsp);
    }
}