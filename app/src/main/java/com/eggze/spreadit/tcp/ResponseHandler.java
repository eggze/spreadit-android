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
package com.eggze.spreadit.tcp;

import android.content.Context;
import android.util.Log;

import androidx.work.WorkManager;

import com.eggze.spreadit.R;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import com.eggze.spreadit.common.packet.server.impl.ServerErrorPacket;
import com.eggze.spreadit.common.packet.server.results.ResultPacket;
import com.eggze.spreadit.common.packet.server.results.SendResultsPacket;
import com.eggze.spreadit.common.packet.server.scans.SendDatesPacket;
import com.eggze.spreadit.common.packet.server.stats.SendStatsPacket;
import com.eggze.spreadit.common.packet.server.user.ServerHelloPacket;
import com.eggze.spreadit.common.packet.server.user.UserCreatedPacket;
import com.eggze.spreadit.common.util.UUIDUtil;
import com.eggze.spreadit.data.database.SpreaditDatabase;
import com.eggze.spreadit.data.database.entity.Stats;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.util.IntentHelper;
import com.eggze.spreadit.util.NotificationHelper;
import com.eggze.spreadit.util.SpreaditExecutors;
import com.eggze.spreadit.work.ScanDatesReceivedWorker;
import com.eggze.spreadit.work.ScanDatesWorker;
import com.eggze.spreadit.work.ScanLocationWorker;
import com.eggze.spreadit.work.StatsPeriodicWorker;
import com.eggze.spreadit.work.UserResultWorker;
import com.eggze.spreadit.work.UserResultsOneTimeWorker;
import com.eggze.spreadit.work.UserResultsPeriodicWorker;

import javax.inject.Singleton;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
@Singleton
public class ResponseHandler {

    private SpreaditDatabase database;
    private SpreaditExecutors executors;
    private final static String TAG = ResponseHandler.class.getSimpleName();
    private Context context;

    public ResponseHandler(Context context, SpreaditDatabase database, SpreaditExecutors executors) {
        this.context = context;
        this.database = database;
        this.executors = executors;
    }

    public final static int STATUS_PENDING = 0;
    public final static int STATUS_OK = 1;

    void handleSuccess(final ServerPacket packet, final ResponseListener responseListener) {
        if (packet != null) {
            executors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    switch (packet.getServerPacketType()) {
                        case ServerPacket.T_USER_CREATED: {
                            User user = new User();
                            user.setId(packet.getPacketIndex());
                            user.setUUID(((UserCreatedPacket) packet).getUserUUID());
                            user.setVersion((packet).getProtocolVersion());
                            database.userDao().upsert(user);
                            StatsPeriodicWorker.scheduleOneTime(context);

                            break;
                        }
                        case ServerPacket.T_SERVER_HELLO: {
                            manageWorkers(false);
                            User user = database.userDao().getById(packet.getPacketIndex());
                            int contactsCount = user.getContactsCount();
                            ServerHelloPacket shp = (ServerHelloPacket) packet;
                            if (contactsCount != shp.getContacts()) {
                                NotificationHelper.showNotification(context,
                                        context.getString(R.string.notification_scan_title),
                                        context.getString(R.string.notification_scan_message),
                                        R.drawable.ic_contact,
                                        "spreadit://spreadit.eggze.com/screens/scans?tab=1",
                                        1);
                                ScanDatesWorker.scheduleOneTime(context);
                            }
                            user.setContactsCount(shp.getContacts());
                            user.setVersion(shp.getProtocolVersion());
                            int positive = shp.getTestResult(); // deprecated, to be removed
                            if (positive > 0) { // deprecated, to be removed
                                user.setPositiveDate(System.currentTimeMillis()); // deprecated, to be removed
                            } // deprecated, to be removed
                            user.setPositives(shp.getTestResult()); // deprecated, to be removed
                            user.setLastUpdate(System.currentTimeMillis());
                            database.userDao().upsert(user);
                            break;
                        }
                        case ServerPacket.T_RESULT_OK: {
                            database.userResultDao().updateStatus(packet.getPacketIndex(), STATUS_OK);
                            break;
                        }
                        case ServerPacket.T_SEND_RESULTS: {
                            SendResultsPacket srp = (SendResultsPacket) packet;

                            if (srp.hasResults()) {
                                int oldCount = database.userResultDao().getWithResultsCount();
                                for (ResultPacket result : srp.getResults()) {
                                    database.userResultDao().updateResult(UUIDUtil.toBytes(result.getResultUUID()), result.getResult(), result.getResultDate());
                                }
                                int newCount = database.userResultDao().getWithResultsCount();
                                if (newCount > oldCount){
                                    NotificationHelper.showNotification(context,
                                            context.getString(R.string.notification_test_title),
                                            context.getString(R.string.notification_test_message),
                                            R.drawable.ic_positive,
                                            "spreadit://spreadit.eggze.com/screens/results?tab=1",
                                            2);
                                }
                            }
                            break;
                        }
                        case ServerPacket.T_SCAN_OK: {
                            database.scanLocationDao().updateStatus(packet.getPacketIndex(), STATUS_OK);
                            break;
                        }
                        case ServerPacketIF.T_SEND_DATES: {
                            SendDatesPacket sdp = (SendDatesPacket) packet;
                            for (long date : sdp.getDates()) {
                                Log.d("DATA", ""+ date);
                                database.scanLocationDao().updateResult(date);
                            }
                            break;
                        }
                        case ServerPacketIF.T_SEND_STATS: {
                            SendStatsPacket ssp = (SendStatsPacket) packet;
                            Stats stats = new Stats();
                            stats.setTests(ssp.getTests());
                            stats.setId(1);
                            stats.setPositives(ssp.getPositives());
                            stats.setLocations(ssp.getLocations());
                            stats.setScans(ssp.getScans());
                            stats.setTestedUsers(ssp.getTestedUsers());
                            stats.setTimestamp(ssp.getStatsTimestamp());
                            stats.setUsers(ssp.getUsers());
                            database.statsDao().upsert(stats);
                        }
                    }
                    if (responseListener != null)
                        responseListener.onSuccess(packet);
                }
            });
        }
    }

    void handleError(final ServerErrorPacket errorPacket, ResponseListener responseListener) {
        if (errorPacket != null) {
            executors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    switch (errorPacket.getErrorID()) {
                        case ServerErrorPacket.E_WRONG_PROTOCOL: {
                            manageWorkers(true);
                            forceUpdate();
                            break;
                        }
                    }
                }
            });
        }
        if (responseListener != null)
            responseListener.onError(errorPacket);
    }

    private void manageWorkers(boolean disable) {
        for (long id : database.scanLocationDao().getPending()) {
            if (disable) {
                WorkManager.getInstance(context).cancelAllWorkByTag(ScanLocationWorker.getTag(id));
            } else {
                ScanLocationWorker.scheduleOneTime(context, id);
            }
        }

        for (long id : database.userResultDao().getPending()) {
            if (disable) {
                WorkManager.getInstance(context).cancelAllWorkByTag(UserResultWorker.getTag(id));
            } else {
                UserResultWorker.scheduleOneTime(context, id);
            }
        }
        if (disable) {
            WorkManager.getInstance(context).cancelAllWorkByTag(UserResultsOneTimeWorker.TAG);
            WorkManager.getInstance(context).cancelAllWorkByTag(UserResultsPeriodicWorker.TAG);
            WorkManager.getInstance(context).cancelAllWorkByTag(ScanDatesWorker.TAG);
            WorkManager.getInstance(context).cancelAllWorkByTag(ScanDatesWorker.TAG_ONE_TIME);
            WorkManager.getInstance(context).cancelAllWorkByTag(StatsPeriodicWorker.TAG);
            WorkManager.getInstance(context).cancelAllWorkByTag(StatsPeriodicWorker.TAG_ONE_TIME);
            WorkManager.getInstance(context).cancelAllWorkByTag(ScanDatesReceivedWorker.TAG_ONE_TIME);
        } else {
            StatsPeriodicWorker.schedulePeriodic(context);
            UserResultsPeriodicWorker.schedulePeriodic(context);
            ScanDatesWorker.schedulePeriodic(context);
        }
    }

    private void forceUpdate() {
        executors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                IntentHelper.forceUpdate(context);
            }
        });
    }
}