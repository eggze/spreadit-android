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

import android.util.Log;

import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.common.packet.client.ClientPacket;
import com.eggze.spreadit.data.database.SpreaditDatabase;
import com.eggze.spreadit.tcp.NetworkManager;
import com.eggze.spreadit.tcp.ResponseListener;
import com.eggze.spreadit.util.SpreaditExecutors;

import java.io.Serializable;

import javax.inject.Inject;

import ch.dermitza.securenio.packet.PacketIF;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class Request implements Serializable {

    @Inject
    SpreaditDatabase database;
    @Inject
    SpreaditExecutors executors;
    @Inject
    NetworkManager networkManager;

    private ClientPacket requestPacket = null;

    private ResponseListener responseListener = null;

    public Request(ResponseListener responseListener) {
        Spreadit.spreaditComponent.inject(this);
        this.responseListener = responseListener;
    }

    public String getTag() {
        return String.valueOf(requestPacket.getClientPacketType()) + requestPacket.getPacketIndex();
    }

    public PacketIF getRequestPacket() {
        return requestPacket;
    }

    void setRequestPacket(ClientPacket requestPacket) {
        this.requestPacket = requestPacket;
    }

    public ResponseListener getResponseListener() {
        return responseListener;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public int getId() {
        return requestPacket.getPacketIndex();
    }

    public void send(){
        if (requestPacket != null) {
            Log.d("STATUS", networkManager.toString());
            networkManager.send(this);
        }
    }
}