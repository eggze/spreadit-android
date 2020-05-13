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
package com.eggze.spreadit.ui.scans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.eggze.spreadit.R;
import com.eggze.spreadit.databinding.FragmentUserScansBinding;
import com.eggze.spreadit.ui.main.BaseFragment;
import com.eggze.spreadit.work.ScanDatesWorker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserScansFragment extends BaseFragment {

    private FragmentUserScansBinding binding = null;
    private final static String ARG_SELECTED_TAB = "ARG_SELECTED_TAB";
    private int selectedTab = 0;
    public static UserScansFragment newInstance(int tab) {
        UserScansFragment fragment = new UserScansFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SELECTED_TAB, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SELECTED_TAB)) {
            selectedTab = args.getInt(ARG_SELECTED_TAB, 0);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.viewPager.setAdapter(new UserScansAdapter(this));
        new TabLayoutMediator(binding.tabL, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setIcon(R.drawable.ic_ok);
                        tab.setText(R.string.scan_result_ok);
                        break;
                    }
                    case 1: {
                        tab.setText(R.string.scan_result_contacts);
                        tab.setIcon(R.drawable.ic_contact);
                        break;
                    }
                }
            }
        }).attach();

        binding.viewPager.post(new Runnable() {
            @Override
            public void run() {
                binding.viewPager.setCurrentItem(selectedTab);
            }
        });
        Observer<List<WorkInfo>> observer = new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                for (WorkInfo workInfo : workInfos) {
                    if (workInfo.getState() == WorkInfo.State.RUNNING) {
                        binding.loadingV.setVisibility(View.VISIBLE);
                    } else {
                        binding.loadingV.setVisibility(View.GONE);
                    }
                }
            }
        };
        WorkManager.getInstance(view.getContext()).getWorkInfosByTagLiveData(ScanDatesWorker.TAG_ONE_TIME)
                .observe(getViewLifecycleOwner(), observer);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserScansBinding.inflate(inflater, container, false);
        binding.loadingV.setVisibility(View.GONE);
        return binding.getRoot();
    }
}