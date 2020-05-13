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
package com.eggze.spreadit;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.eggze.spreadit.di.DaggerSpreaditComponent;
import com.eggze.spreadit.di.DatabaseModule;
import com.eggze.spreadit.di.ExecutorsModule;
import com.eggze.spreadit.di.NetworkModule;
import com.eggze.spreadit.di.SpreaditComponent;
import com.eggze.spreadit.util.NotificationHelper;
import com.eggze.spreadit.util.PRNGFixes;
import com.eggze.spreadit.work.HelloPeriodicWorker;
import com.eggze.spreadit.work.UserResultsPeriodicWorker;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class Spreadit extends Application {

    private final static String TAG = Spreadit.class.getSimpleName();
    public static SpreaditComponent spreaditComponent = null;
    public static DateFormat dateTimeFormat;
    public static DateFormat dateFormat;
    public static NumberFormat numberFormat;

    static {
        dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        numberFormat = NumberFormat.getInstance(Locale.getDefault());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (patchSecurityProvider()) {
            try {
                Log.i(TAG, "PRNGFixes: Attempting to apply");
                PRNGFixes.apply();
                Log.i(TAG, "PRNGFixes: Success");
                spreaditComponent = initializeComponent();
                scheduleWorkers();
                NotificationHelper.createNotificationChannel(this);
                return;
            } catch (SecurityException se) {
                Log.w(TAG, "PRNGFixes: Failed");
            }

        }
        // Application exit. Re-attempt on application restart
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private SpreaditComponent initializeComponent() {
        return DaggerSpreaditComponent.builder()
                .application(this)
                .context(this)
                .databaseModule(new DatabaseModule(this))
                .executorsModule(new ExecutorsModule())
                .networkModule(new NetworkModule())
                .build();
    }

    private void scheduleWorkers() {
        HelloPeriodicWorker.schedulePeriodic(this);
        UserResultsPeriodicWorker.schedulePeriodic(this);
    }

    /**
     * Attempt to "patch-in" TLS1.2 support with a more recent SecurityProvider from the Google
     * Services API. On API 15 (tested), with the latest/last working google play services available
     * (14.7.99) for API 14, this replaces the local phone security provider with GmsCore_OpenSSL.
     * The replacement seems to not be persistent across device (and possibly application?) restarts.
     * <p>
     * Still open point if this is needed (as "good practice") in general or only for older android
     * versions. Currently only attempting the patch for API levels lower than API18 to also
     * correspond with the PRNGFixes being applied afterwards.
     */
    private boolean patchSecurityProvider() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                Log.e(TAG, "ProviderInstaller installIfNeeded()");
                ProviderInstaller.installIfNeeded(getApplicationContext());
                // If this is reached, you know that the provider was already up-to-date,
                // or was successfully updated.
                Log.e(TAG, "ProviderInstaller install succeeded");
                return true;
            } catch (GooglePlayServicesRepairableException e) {
                Log.w(TAG, "ProviderInstaller install failed, RepairableException " + e.getLocalizedMessage());
                // Indicates that Google Play services is out of date, disabled, etc.
                // Prompt the user to install/update/enable Google Play services.
                GoogleApiAvailability.getInstance()
                        .showErrorNotification(Spreadit.this, e.getConnectionStatusCode());
                // Need to retry, TODO
                return false;
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.w(TAG, "ProviderInstaller install failed, NotAvailableException " + e.getLocalizedMessage());
                // Indicates a non-recoverable error; the ProviderInstaller is not able
                // to install an up-to-date Provider. No availability of TLS1.2, as such, the app
                // should fail. Possibly with a notice? TODO
                return false;
            }
        }
        // No need to do any patching
        return true;
    }
}
