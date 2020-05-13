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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.eggze.spreadit.data.database.entity.Stats;
import com.eggze.spreadit.databinding.FragmentStatsBinding;
import com.eggze.spreadit.work.StatsPeriodicWorker;

import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class StatsFragment extends BaseFragment {

    private FragmentStatsBinding binding = null;


    public static StatsFragment newInstance(int index) {
        StatsFragment fragment = new StatsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkManager.getInstance(view.getContext()).getWorkInfosByTagLiveData(StatsPeriodicWorker.TAG)
                .observe(getViewLifecycleOwner(), new Observer<List<WorkInfo>>() {
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
                });

        database.statsDao().fetchById(1).observe(getViewLifecycleOwner(), new Observer<Stats>() {
            @Override
            public void onChanged(Stats stats) {
                if (stats == null) {
                    binding.mainL.setVisibility(View.GONE);
                    binding.emptyL.getRoot().setVisibility(View.VISIBLE);
                } else {
                    binding.setStats(stats);
                    binding.mainL.setVisibility(View.VISIBLE);
                    binding.emptyL.getRoot().setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        binding.loadingV.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}