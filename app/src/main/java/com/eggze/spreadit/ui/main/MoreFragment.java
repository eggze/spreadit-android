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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.eggze.spreadit.BuildConfig;
import com.eggze.spreadit.R;
import com.eggze.spreadit.activities.TermsActivity;
import com.eggze.spreadit.common.util.scan.ClientScanCodec;
import com.eggze.spreadit.common.util.scan.ResultScan;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.data.database.entity.UserResult;
import com.eggze.spreadit.data.enums.UserTestStatus;
import com.eggze.spreadit.databinding.FragmentMoreBinding;
import com.eggze.spreadit.ui.results.UserResultsFragment;
import com.eggze.spreadit.util.IntentHelper;
import com.eggze.spreadit.work.UserResultWorker;
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
public class MoreFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {

    private static final int RC_CAMERA_PERM = 101;
    private final static long TEST_LIMIT = 60 * 1000;
    private String userID = null;

    private FragmentMoreBinding binding = null;

    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        binding.setOnClickListener(this);
        binding.aboutL.setSubtitle(String.format(getString(R.string.more_about_subtitle), BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE+ ")"));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database.userDao().fetchActiveUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    userID = user.getUUID();
                    binding.setUserID(userID);
                }
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.add_test_l: {
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        UserResult latest = database.userResultDao().getLast();
                        if (BuildConfig.DEBUG || latest == null || latest.getScanTimestamp() < System.currentTimeMillis() - TEST_LIMIT) {
                            scanQR();
                        } else {
                            executors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialAlertDialogBuilder(v.getContext())
                                            .setTitle(R.string.error_title)
                                            .setMessage(R.string.test_limit)
                                            .setNegativeButton(R.string.action_ok, null)
                                            .show();
                                }
                            });
                        }
                    }
                });
                break;
            }
            case R.id.tests_list_l: {
                navigateToList();
                break;
            }
            case R.id.terms_l: {
                Intent intent = new Intent(getActivity(), TermsActivity.class);
                intent.putExtra(TermsActivity.SHOW_ACTION, false);
                startActivity(intent);
                break;
            }
            case R.id.about_l: {
                IntentHelper.openUrl(v.getContext(), getString(R.string.more_about_url));
                break;
            }
            case R.id.contact_l: {
                String userID;
                if (this.userID == null || this.userID.length() == 0) {
                    userID = "\n\n ---------------- \n\n NUIDA";
                } else {
                    userID = "\n\n ---------------- \n\n " + this.userID;
                }

                IntentHelper.sendEmail(getActivity(), getString(R.string.more_contact_title), getString(R.string.more_contact_email), getString(R.string.app_name) + " " + getString(R.string.more_contact_title), userID);
                break;
            }
            case R.id.from_iv: {
                IntentHelper.openUrl(v.getContext(), getString(R.string.more_eggze_url));
                break;
            }
            case R.id.from_b_iv: {
                IntentHelper.openUrl(v.getContext(), getString(R.string.more_kalyvas_url));
                break;
            }
            case R.id.user_l: {
                if (this.userID != null && this.userID.length() > 0) {
                    ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        ClipData clip = ClipData.newPlainText("spreadit_userID", userID);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(v.getContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    private void parseQR(final String data) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ResultScan resDecoded = ClientScanCodec.resultScanFromBase64(data);
                    if (resDecoded == null) {
                        throw new Exception();
                    } else {
                        saveTest(resDecoded.getScanUUID().toString(), resDecoded.getScanSerial());
                    }

                } catch (Exception e) {
                    showQRError(getString(R.string.more_test_scan_error));
                }
            }
        });
    }

    private void saveTest(String testUUID, int serial) {
        UserResult userResult = new UserResult();
        userResult.setTestResult(UserTestStatus.PENDING.ordinal());
        userResult.setUUID(UUID.fromString(testUUID));
        userResult.setSerial(serial);
        userResult.setScanTimestamp(System.currentTimeMillis());
        long id = database.userResultDao().insert(userResult);
        if (id > -1) {
            UserResultWorker.scheduleOneTime(getContext(), id);
            executors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    navigateToList();
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
            intent.putExtra(Intents.Scan.PROMPT_MESSAGE, getString(R.string.more_test_scan_info));
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
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
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

    private void navigateToList() {
        getParentFragmentManager().beginTransaction()
                .add(R.id.container_l, UserResultsFragment.newInstance(0), UserResultsFragment.class.getCanonicalName())
                .addToBackStack(UserResultsFragment.class.getCanonicalName()).commit();
    }
}