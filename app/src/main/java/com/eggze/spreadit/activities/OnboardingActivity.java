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
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.eggze.spreadit.R;
import com.eggze.spreadit.databinding.ActivityOnboardingBinding;
import com.eggze.spreadit.ui.onboarding.OnboardingAdapter;
import com.eggze.spreadit.util.OnboardingType;
import com.eggze.spreadit.util.Statics;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class OnboardingActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private static final int TOTAL = 4;
    private ActivityOnboardingBinding binding = null;
    private SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        sp = getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);
        binding.setOnClickListener(this);
        binding.introViewPager.setAdapter(new OnboardingAdapter(this, generateData()));
        binding.introViewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == TOTAL - 1) {
//                    binding.doneBtn.setText(R.string.done);
                    binding.doneBtn.setIcon(null);
                    binding.skipBtn.setVisibility(View.GONE);
                    binding.doneBtn.setEnabled(sp.getBoolean(Statics.ACCEPTED_TERMS, false));
                } else {
                    binding.doneBtn.setEnabled(true);
                    binding.skipBtn.setVisibility(View.VISIBLE);
                    binding.doneBtn.setIconResource(R.drawable.ic_chevron_right_white);
                    binding.doneBtn.setText("");
                }
            }
        });
        new TabLayoutMediator(binding.tabLayout, binding.introViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();
    }

    private ArrayList<Bundle> generateData() {
        ArrayList<Bundle> data = new ArrayList<>();
        for (int i = 0; i < TOTAL; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(Statics.ARG_ONBOARDING_INDEX, i);
            bundle.putInt(Statics.ARG_ONBOARDING_TITLE, getResources().getIdentifier("onboarding_title_" + i, "string", getPackageName()));
            bundle.putInt(Statics.ARG_ONBOARDING_INFO, getResources().getIdentifier("onboarding_info_" + i, "string", getPackageName()));
            bundle.putInt(Statics.ARG_ONBOARDING_COLOR, getResources().getIdentifier("onboarding_bg_" + i, "color", getPackageName()));
            bundle.putSerializable(Statics.ARG_ONBOARDING_TYPE, OnboardingType.values()[i]);
            bundle.putInt(Statics.ARG_ONBOARDING_IMAGE, getResources().getIdentifier("onboarding_info_" + i, "string", getPackageName()));
            data.add(bundle);
        }
        return data;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Statics.ACCEPTED_TERMS)) {
            binding.doneBtn.setEnabled(sp.getBoolean(Statics.ACCEPTED_TERMS, false));
        }
    }

    private void goToMain() {
        final SharedPreferences sp = getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);
        sp.edit().putBoolean(Statics.ONBOARDING_COMPLETED, true).apply();
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_btn:
                int currentItem = binding.introViewPager.getCurrentItem();
                if (currentItem < TOTAL - 1) {
                    binding.introViewPager.setCurrentItem(currentItem + 1, true);
                } else {
                    goToMain();
                }
                break;

            case R.id.skip_btn:
                binding.introViewPager.setCurrentItem(3, true);
                break;
        }
    }
}
