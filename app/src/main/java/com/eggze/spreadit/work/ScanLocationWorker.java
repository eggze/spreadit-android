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
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.WorkerParameters;

import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.common.packet.client.ClientPacket;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.impl.ServerErrorPacket;
import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.tcp.ResponseListener;
import com.eggze.spreadit.tcp.TCPStatusListener;
import com.eggze.spreadit.tcp.requests.Request;
import com.eggze.spreadit.tcp.requests.ScanLocationRequest;
import com.eggze.spreadit.util.Statics;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.eggze.spreadit.common.packet.server.impl.ServerErrorPacketIF.E_SCAN_INVALID;
import static com.eggze.spreadit.tcp.ResponseHandler.STATUS_OK;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class ScanLocationWorker extends BaseWorker {
    public ScanLocationWorker(Context appContext, WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @Keep
    private final static long INITIAL_DELAY_INTERVAL = 0;
    @Keep
    private final static long BACKOFF_INTERVAL = 10;

    public final static String TAG = BaseWorker.PREFIX + "SCAN_";

    public static void scheduleOneTime(Context context, long id) {
        Data data = new Data.Builder().putLong("id", id).build();
        scheduleOneTime(context, getTag(id), ScanLocationWorker.class,
                data, INITIAL_DELAY_INTERVAL, TimeUnit.SECONDS,
                BackoffPolicy.EXPONENTIAL, BACKOFF_INTERVAL, TimeUnit.MINUTES, ExistingWorkPolicy.KEEP);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        Log.d("ScanLocationWorker", "startWork");
        Spreadit.spreaditComponent.inject(this);
        final SharedPreferences sp = getApplicationContext().getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);

        final long id = getInputData().getLong("id", -1);

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
                        switch (errorPacket.getClientPacketType()) {
                            case ClientPacket.T_USER_SCAN: {
                                if (errorPacket.getErrorID() == E_SCAN_INVALID) {
                                    database.scanLocationDao().updateStatus(errorPacket.getPacketIndex(), STATUS_OK);
                                    completer.set(Result.success());
                                }
                                break;
                            }
                        }
                        completer.set(Result.retry());
                    }
                };
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final ScanLocation scanLocation = database.scanLocationDao().getById(id);
                        final User user = database.userDao().getActiveUser();

                        if (id == -1 || scanLocation == null || user == null) {
                            completer.set(Result.failure());
                        } else {
                            if (!networkManager.isConnected()) {
                                final TCPStatusListener tcpStatusListener = new TCPStatusListener() {
                                    @Override
                                    public void statusChanged(int status) {
                                        switch (status) {
                                            case STATUS_CONNECTING: {
                                                Log.d("ScanLocationWorker", "STATUS_CONNECTING");
                                                Log.d("ScanLocationWorker", "client " + networkManager.getClient().toString());
                                                break;
                                            }
                                            case STATUS_DISCONNECTED: {
                                                Log.d("ScanLocationWorker", "STATUS_DISCONNECTED");
                                                Log.d("ScanLocationWorker", "client " + networkManager.getClient().toString());
                                                break;
                                            }
                                            case STATUS_CONNECTED: {
                                                Log.d("ScanLocationWorker", "STATUS_CONNECTED");
                                                if (sp.getBoolean(Statics.ACCEPTED_TERMS, false)) {
                                                    Log.d("ScanLocationWorker", "SEND");
                                                    Log.d("ScanLocationWorker", "client " + networkManager.getClient().toString());

                                                    send(user, scanLocation, responseListener);
                                                } else {
                                                    completer.set(Result.success());
                                                }
                                                networkManager.unregisterStatusListener(this);
                                                break;
                                            }
                                            case STATUS_CONN_FAILED: {
                                                Log.d("ScanLocationWorker", "STATUS_CONN_FAILED");
                                                Log.d("ScanLocationWorker", "client " + networkManager.getClient().toString());
                                                completer.set(Result.retry());
                                                networkManager.unregisterStatusListener(this);
                                                break;
                                            }
                                        }
                                    }
                                };
                                networkManager.registerStatusListener(tcpStatusListener);
                                networkManager.tryConnect();
                            } else {
                                Log.d("ScanLocationWorker", "WAS_CONNECTED");
                                if (sp.getBoolean(Statics.ACCEPTED_TERMS, false)) {
                                    Log.d("ScanLocationWorker", "SEND");
                                    send(user, scanLocation, responseListener);
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

    private void send(final User user, final ScanLocation scanLocation, final ResponseListener responseListener) {
        Request request = new ScanLocationRequest(user, scanLocation, responseListener);
        request.send();
    }

    public static String getTag(long id) {
        return TAG + id;
    }

}
