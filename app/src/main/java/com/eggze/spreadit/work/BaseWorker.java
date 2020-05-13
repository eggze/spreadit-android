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

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import com.eggze.spreadit.data.database.SpreaditDatabase;
import com.eggze.spreadit.tcp.NetworkManager;
import com.eggze.spreadit.util.SpreaditExecutors;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
abstract public class BaseWorker extends ListenableWorker {

    @Inject
    public NetworkManager networkManager;

    @Inject
    public SpreaditDatabase database;

    @Inject
    public SpreaditExecutors executors;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public BaseWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @Keep

    final static String PREFIX = "SP_WORK_";

    @Keep
    final static long REPEAT_INTERVAL = 6L;

    @Keep
    private final static long FLEX_INTERVAL = 30L;

    @Keep
    private final static TimeUnit REPEAT_TIME_UNIT = TimeUnit.HOURS;
    private final static TimeUnit FLEX_TIME_UNIT = TimeUnit.MINUTES;


    @Keep
    static void schedule(Context context, String tag, Class<? extends ListenableWorker> workerClass, Data data,
                                long repeatInterval, TimeUnit repeatTimeUnit,
                                long flexInterval, TimeUnit flexTimeUnit,
                                long initialDelay, TimeUnit delayTimeUnit,
                                BackoffPolicy backoffPolicy, long backoffDelay, TimeUnit backoffTimeUnit) {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build();
        PeriodicWorkRequest.Builder builder =
                new PeriodicWorkRequest.Builder(workerClass, repeatInterval,
                        repeatTimeUnit, flexInterval, flexTimeUnit)
                        .setConstraints(constraints).addTag(tag);
        if (data != null) {
            builder.setInputData(data);
        }
        if (initialDelay > 0) {
            builder.setInitialDelay(initialDelay, delayTimeUnit);
        }
        builder.setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit);
        PeriodicWorkRequest request = builder.build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    @Keep
    static void scheduleOneTime(Context context, String tag,
                                Class<? extends ListenableWorker> workerClass, Data data,
                                long initialDelay, TimeUnit delayTimeUnit,
                                BackoffPolicy backoffPolicy, long backoffDelay, TimeUnit backoffTimeUnit, ExistingWorkPolicy policy) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build();
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(workerClass)
                .setConstraints(constraints).addTag(tag);
        if (data != null) {
            builder.setInputData(data);
        }
        if (initialDelay > 0) {
            builder.setInitialDelay(initialDelay, delayTimeUnit);
        }
        builder.setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit);
        OneTimeWorkRequest request = builder.build();
        WorkManager.getInstance(context).enqueueUniqueWork(tag, policy, request);
    }
}