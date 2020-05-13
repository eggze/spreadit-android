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
package com.eggze.spreadit.work;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.WorkerParameters;

import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.impl.ServerErrorPacket;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.tcp.ResponseListener;
import com.eggze.spreadit.tcp.TCPStatusListener;
import com.eggze.spreadit.tcp.requests.Request;
import com.eggze.spreadit.tcp.requests.ScanDatesReceivedRequest;
import com.eggze.spreadit.util.Statics;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class ScanDatesReceivedWorker extends BaseWorker {
    public ScanDatesReceivedWorker(Context appContext, WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    public final static String TAG_ONE_TIME = BaseWorker.PREFIX + "SCAN_DATES_RECEIVED_ONE_TIME";

    private final static String EXTRA_DATA_TIMESTAMP = "timestamp";
    @Keep
    private final static long INITIAL_DELAY_INTERVAL = 2L;
    @Keep
    private final static long BACKOFF_INTERVAL = 2;

    public static void scheduleOneTime(Context context, long timestamp) {
        Data data = new Data.Builder().putLong(EXTRA_DATA_TIMESTAMP, timestamp).build();
        scheduleOneTime(context, TAG_ONE_TIME, ScanDatesReceivedWorker.class,
                data, INITIAL_DELAY_INTERVAL, TimeUnit.SECONDS,
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_INTERVAL, TimeUnit.MINUTES, ExistingWorkPolicy.REPLACE);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        Spreadit.spreaditComponent.inject(this);
        final SharedPreferences sp = getApplicationContext().getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);
        final long timestamp = getInputData().getLong(EXTRA_DATA_TIMESTAMP, -1);

        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull final CallbackToFutureAdapter.Completer<Result> completer) throws Exception {
                final ResponseListener responseListener = new ResponseListener() {
                    @Override
                    public void onSuccess(ServerPacket packet) {
                        completer.set(Result.success());
                    }

                    @Override
                    public void onError(final ServerErrorPacket errorPacket) {
                        completer.set(Result.success());
                    }
                };
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int count = database.scanLocationDao().getCount();

                        if (count == 0) {
                            completer.set(Result.success());
                        }

                        final User user = database.userDao().getActiveUser();
                        if (user == null || timestamp == -1) {
                            completer.set(Result.success());
                        } else {
                            if (!networkManager.isConnected()) {
                                networkManager.tryConnect();
                                final TCPStatusListener tcpStatusListener = new TCPStatusListener() {
                                    @Override
                                    public void statusChanged(int status) {
                                        switch (status) {
                                            case STATUS_CONNECTED: {
                                                if (sp.getBoolean(Statics.ACCEPTED_TERMS, false)) {
                                                    send(user, timestamp, responseListener);
                                                } else {
                                                    completer.set(Result.success());
                                                }
                                                networkManager.unregisterStatusListener(this);
                                                break;
                                            }
                                            case STATUS_CONN_FAILED: {
                                                completer.set(Result.retry());
                                                networkManager.unregisterStatusListener(this);
                                                break;
                                            }
                                        }
                                    }
                                };
                                networkManager.registerStatusListener(tcpStatusListener);
                            } else {
                                if (sp.getBoolean(Statics.ACCEPTED_TERMS, false)) {
                                    send(user, timestamp, responseListener);
                                } else {
                                    completer.set(Result.success());
                                }
                            }
                        }
                    }
                });
                return responseListener;
            }
        });
    }

    private void send(final User user, long timestamp, final ResponseListener responseListener) {
        Request request = new ScanDatesReceivedRequest(user, timestamp, responseListener);
        request.send();
    }
}
