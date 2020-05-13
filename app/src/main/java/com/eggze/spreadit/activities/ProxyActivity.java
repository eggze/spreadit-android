/**
 *  This file is part of spreadit-android. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.eggze.spreadit.util.Statics;

public class ProxyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        Uri data = null;
        if (intent !=null){
            data = intent.getData();
        }
        if (hasCompletedOnboarding()) {
            final Intent nextActivity = new Intent(this, MainActivity.class);
            nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            nextActivity.setData(data);
            startActivity(nextActivity);
        } else {
            final Intent nextActivity = new Intent(this, OnboardingActivity.class);
            nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            nextActivity.setData(data);
            startActivity(nextActivity);
        }
        setIntent(null);
        finish();
    }
    private boolean hasCompletedOnboarding() {
        final SharedPreferences sp = getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);
        return sp.getBoolean(Statics.ONBOARDING_COMPLETED, false);
    }
}
