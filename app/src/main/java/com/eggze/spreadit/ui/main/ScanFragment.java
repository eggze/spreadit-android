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
package com.eggze.spreadit.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eggze.spreadit.BuildConfig;
import com.eggze.spreadit.R;
import com.eggze.spreadit.common.util.scan.ClientScanCodec;
import com.eggze.spreadit.common.util.scan.LocationScan;
import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.databinding.FragmentScanBinding;
import com.eggze.spreadit.ui.scans.UserScansFragment;
import com.eggze.spreadit.work.ScanLocationWorker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static android.app.Activity.RESULT_OK;
import static com.eggze.spreadit.util.Statics.INTENT_ACTION_SCAN;
import static com.eggze.spreadit.util.Statics.INTENT_EXTRA_SCAN_RESULT;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class ScanFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    private static final int RC_CAMERA_PERM = 100;
    private FragmentScanBinding binding = null;
    private final static long SCAN_LIMIT = 60 * 1000;

    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        binding.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.btnScanQr.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pulse));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_scan_qr:
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        ScanLocation latest = database.scanLocationDao().getLast();
                        if (BuildConfig.DEBUG || latest == null || latest.getScanTimestamp() < System.currentTimeMillis() - SCAN_LIMIT) {
                            scanQR();
                        } else {
                            executors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialAlertDialogBuilder(v.getContext())
                                            .setTitle(R.string.error_title)
                                            .setMessage(R.string.scan_limit)
                                            .setNegativeButton(R.string.action_ok, null)
                                            .show();
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.btn_scan_history: {
                toScanList(0);
                break;
            }
            default:
                super.onClick(v);
        }
    }

    private void toScanList(int page){
        getParentFragmentManager().beginTransaction()
                .add(R.id.container_l, UserScansFragment.newInstance(page), UserScansFragment.class.getCanonicalName())
                .addToBackStack(UserScansFragment.class.getCanonicalName()).commit();
    }

    private void parseQR(final String data) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationScan resDecoded = ClientScanCodec.locationScanFromBase64(data);
                    if (resDecoded == null) {
                        throw new Exception();
                    } else {
                        saveLocation(resDecoded.getScanUUID().toString(), resDecoded.getLat(), resDecoded.getLon());
                    }

                } catch (Exception e) {
                    showQRError(getString(R.string.more_test_scan_error));
                }
            }
        });
    }

    private void saveLocation(String locUUID, float lat, float lon) {
        ScanLocation scanLocation = new ScanLocation();
        scanLocation.setLocationUUID(UUID.fromString(locUUID));
        scanLocation.setUUID(UUID.randomUUID());
        scanLocation.setLatitude(lat);
        scanLocation.setLongitude(lon);
        final long id = database.scanLocationDao().insert(scanLocation);
        if (id > -1) {
            executors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ScanLocationWorker.scheduleOneTime(getContext(), id);
                    executors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            toScanList(0);
                        }
                    });
                }
            });
        } else {
            showQRError(getString(R.string.test_exists));
        }
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void scanQR() {
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.CAMERA)) {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            intent.setAction(INTENT_ACTION_SCAN);
            intent.putExtra(Intents.Scan.PROMPT_MESSAGE, getString(R.string.more_location_scan_info));
            intent.putExtra(Intents.Scan.SAVE_HISTORY, false);
            startActivityForResult(intent, RC_CAMERA_PERM);
        } else {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this,
                    RC_CAMERA_PERM, Manifest.permission.CAMERA)
                    .setRationale(getString(R.string.permission_rationale_camera))
                    .setPositiveButtonText(getString(R.string.action_ok))
                    .setNegativeButtonText(getString(R.string.action_cancel))
                    .build());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE
                && EasyPermissions.hasPermissions(getContext(), Manifest.permission.CAMERA)) {
            scanQR();
        } else if (requestCode == RC_CAMERA_PERM
                && resultCode == RESULT_OK
                && data != null
                && data.getAction() != null
                && data.getAction().equals(INTENT_ACTION_SCAN)
                && data.getExtras() != null
                && data.getExtras().containsKey(INTENT_EXTRA_SCAN_RESULT)) {
            parseQR(data.getStringExtra(INTENT_EXTRA_SCAN_RESULT));
        } else {
            if (resultCode == RESULT_OK) {
                showQRError(getString(R.string.more_test_scan_error));
            }
        }
    }

    private void showQRError(final String subtitle) {
        executors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.error_title)
                        .setMessage(subtitle)
                        .setNegativeButton(R.string.action_ok, null)
                        .show();
            }
        });
    }
}
