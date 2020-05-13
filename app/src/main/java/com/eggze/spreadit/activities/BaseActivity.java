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

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.tcp.NetworkManager;
import com.eggze.spreadit.viewmodel.TCPClientViewModel;

import javax.inject.Inject;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    NetworkManager networkManager;
    private final static String TAG = BaseActivity.class.getSimpleName();
    protected TCPClientViewModel tcpClientViewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        // https://stackoverflow.com/questions/37615470/support-library-vectordrawable-resourcesnotfoundexception
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Spreadit.spreaditComponent.inject(this);
        tcpClientViewModel = new ViewModelProvider(this).get(TCPClientViewModel.class);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume()");
        super.onResume();
        networkManager.onResume();
        tcpClientViewModel.connectClient();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop()");
        networkManager.onStop();
        tcpClientViewModel.disconnectClient();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy()");
        // Do not disconnect the client onDestroy(), as it can happen that there is still a/multiple
        // threads queued to send more data. Instead, let disconnection be handled by the timer,
        // as soon as all threads have stopped asking for their respective data transfers.
        //networkManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onUserInteraction() {
        Log.e(TAG, "onUserInteraction()");
        networkManager.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.e(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }
}