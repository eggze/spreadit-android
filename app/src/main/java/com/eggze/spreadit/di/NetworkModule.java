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
package com.eggze.spreadit.di;

import android.content.Context;

import com.eggze.spreadit.data.database.SpreaditDatabase;
import com.eggze.spreadit.tcp.NetworkManager;
import com.eggze.spreadit.tcp.ResponseHandler;
import com.eggze.spreadit.util.SpreaditExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
@Module
public class NetworkModule {

    public NetworkModule() {
    }

    @Provides
    @Singleton
    @Inject
    public NetworkManager providesNetworkManager(Context context, SpreaditExecutors executors, ResponseHandler responseHandler) {
        return new NetworkManager(context, executors, responseHandler);
    }

    @Provides
    @Singleton
    @Inject
    public ResponseHandler providesResponseHandler(Context context, SpreaditDatabase database, SpreaditExecutors executors) {
        return new ResponseHandler(context, database, executors);
    }
}