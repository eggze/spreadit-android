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

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.eggze.spreadit.R;
import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.databinding.FragmentMainBinding;
import com.eggze.spreadit.ui.results.UserResultsFragment;
import com.eggze.spreadit.ui.scans.UserScansFragment;
import com.eggze.spreadit.util.Statics;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class MainFragment extends BaseFragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FragmentMainBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Spreadit.spreaditComponent.inject(this);
        binding = FragmentMainBinding.inflate(inflater, container, false);
        binding.setOnClickListener(this);
        binding.viewPager.setUserInputEnabled(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity());
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.navigation_main);
        toggleUserStatus();
    }

    private void toggleUserStatus() {
        database.userDao().fetchActiveUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(final User user) {
                if (user != null) {
                    database.userResultDao().fetchPositiveCount(System.currentTimeMillis() - Statics.EXPIRATION).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer count) {
                            if (count != null && count > 0) {
                                binding.userStatusL.setTag(2);
                                binding.userStatusTxtv.setText(R.string.positive);
                                binding.userStatusIv.setImageResource(R.drawable.ic_virus);
                                binding.userStatusLastUpdateTxtv.setText(null);
                                binding.userStatusLastUpdateTxtv.setVisibility(View.GONE);
                                binding.userStatusL.setBackgroundResource(R.drawable.bg_positive);
                                binding.userStatusL.setVisibility(View.VISIBLE);
                            } else if (user.getContactsCount() > 0) {
                                binding.userStatusL.setTag(1);
                                binding.userStatusIv.setImageResource(R.drawable.ic_contact);
                                if (user.getContactsCount() == 1) {
                                    binding.userStatusTxtv.setText(String.format(getString(R.string.contacted_one), user.getContactsCount()));
                                } else {
                                    binding.userStatusTxtv.setText(String.format(getString(R.string.contacted_many), user.getContactsCount()));
                                }
                                binding.userStatusLastUpdateTxtv.setVisibility(View.VISIBLE);
                                binding.userStatusLastUpdateTxtv.setText(String.format(getString(R.string.last_update), Spreadit.dateTimeFormat.format(new Date(user.getLastUpdate()))));
                                binding.userStatusL.setBackgroundResource(R.drawable.bg_contact);
                                binding.userStatusL.setVisibility(View.VISIBLE);
                            } else {
                                binding.userStatusL.setTag(0);
                                binding.userStatusL.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int pos = 0;
        switch (item.getItemId()) {
            case (R.id.navigation_main):
                toggleUserStatus();
                pos = 0;
                break;
            case (R.id.navigation_stats):
                toggleInfoCard(View.GONE, 250);
                pos = 1;
                break;
            case (R.id.navigation_settings):
                toggleInfoCard(View.GONE, 0);
                pos = 2;
                break;
        }
        binding.viewPager.setCurrentItem(pos, false);
        return true;
    }

    private void toggleInfoCard(final int visibility, int delay) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.userStatusL.setVisibility(visibility);
                }
            }, delay);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_status_l) {
            int tag = (int) v.getTag();
            if (tag == 1) {
                getParentFragmentManager().beginTransaction()
                        .add(R.id.container_l, UserScansFragment.newInstance(1), UserScansFragment.class.getCanonicalName())
                        .addToBackStack(UserScansFragment.class.getCanonicalName()).commit();
            } else if (tag == 2) {
                getParentFragmentManager().beginTransaction()
                        .add(R.id.container_l, UserResultsFragment.newInstance(1), UserResultsFragment.class.getCanonicalName())
                        .addToBackStack(UserResultsFragment.class.getCanonicalName()).commit();
            }
        }
    }
}
