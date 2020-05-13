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

import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.data.enums.ScanLocationPageType;
import com.eggze.spreadit.databinding.FragmentUserScansPageBinding;
import com.eggze.spreadit.ui.main.BaseFragment;

import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserScansPageFragment extends BaseFragment {

    private FragmentUserScansPageBinding binding = null;
    private UserScansPageAdapter adapter = null;
    private ScanLocationPageType type = null;

    public static UserScansPageFragment newInstance(ScanLocationPageType type) {
        UserScansPageFragment fragment = new UserScansPageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ScanLocationPageType.class.getCanonicalName(), type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleArgs();
        Observer<List<ScanLocation>> observer = new Observer<List<ScanLocation>>() {
            @Override
            public void onChanged(List<ScanLocation> data) {
                setData(data);
            }
        };
        if (type == ScanLocationPageType.OK) {
            database.scanLocationDao().fetchResultPending().observe(getViewLifecycleOwner(), observer);
        } else {
            database.scanLocationDao().fetchWithResults().observe(getViewLifecycleOwner(), observer);
        }
    }

    private void setData(List<ScanLocation> data) {
        binding.setData(data);
        if (adapter == null) {
            adapter = new UserScansPageAdapter(data);
            binding.rv.setAdapter(adapter);
        } else {
            adapter.setData(data);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserScansPageBinding.inflate(inflater, container, false);
        binding.emptyL.getRoot().setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ScanLocationPageType.class.getCanonicalName())) {
            type = (ScanLocationPageType) args.getSerializable(ScanLocationPageType.class.getCanonicalName());
        }
    }
}