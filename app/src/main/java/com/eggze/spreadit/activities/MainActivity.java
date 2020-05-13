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
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.eggze.spreadit.BuildConfig;
import com.eggze.spreadit.R;
import com.eggze.spreadit.databinding.ActivityMainBinding;
import com.eggze.spreadit.ui.main.MainFragment;
import com.eggze.spreadit.ui.results.UserResultsFragment;
import com.eggze.spreadit.ui.scans.UserScansFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (!BuildConfig.DEBUG) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        getSupportFragmentManager().beginTransaction().replace(binding.containerL.getId(), new MainFragment()).commit();
        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null && data.getEncodedPath() != null && data.getEncodedPath().contains("screens")) {
                int selectedTab = 0;
                try {
                    String tab = data.getQueryParameter("tab");
                    if (tab != null) {
                        selectedTab = Integer.parseInt(tab);
                    }
                } catch (Exception e) {

                }
                if (data.getEncodedPath().contains("scans")) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container_l, UserScansFragment.newInstance(selectedTab), UserScansFragment.class.getCanonicalName())
                            .addToBackStack(UserScansFragment.class.getCanonicalName()).commit();
                } else if (data.getEncodedPath().contains("results")) {

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container_l, UserResultsFragment.newInstance(selectedTab), UserResultsFragment.class.getCanonicalName())
                            .addToBackStack(UserResultsFragment.class.getCanonicalName()).commit();
                }
            }
        }
        setIntent(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}