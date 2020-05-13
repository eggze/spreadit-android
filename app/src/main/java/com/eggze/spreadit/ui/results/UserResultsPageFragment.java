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
package com.eggze.spreadit.ui.results;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.eggze.spreadit.data.database.entity.UserResult;
import com.eggze.spreadit.data.enums.UserResultPageType;
import com.eggze.spreadit.databinding.FragmentUserResultsPageBinding;
import com.eggze.spreadit.ui.main.BaseFragment;

import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserResultsPageFragment extends BaseFragment {

    private FragmentUserResultsPageBinding binding = null;
    private UserResultsPageAdapter adapter = null;
    private UserResultPageType type = null;

    public static UserResultsPageFragment newInstance(UserResultPageType type) {
        UserResultsPageFragment fragment = new UserResultsPageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(UserResultPageType.class.getCanonicalName(), type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleArgs();
        Observer<List<UserResult>> observer = new Observer<List<UserResult>>() {
            @Override
            public void onChanged(List<UserResult> userResults) {
                setData(userResults);
            }
        };
        if (type == UserResultPageType.PENDING) {
            database.userResultDao().fetchResultPending().observe(getViewLifecycleOwner(), observer);
        } else {
            database.userResultDao().fetchWithResults().observe(getViewLifecycleOwner(), observer);
        }
    }

    private void setData(List<UserResult> userResults) {
        binding.setData(userResults);
        if (adapter == null) {
            adapter = new UserResultsPageAdapter(userResults);
            binding.rv.setAdapter(adapter);
        } else {
            adapter.setData(userResults);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserResultsPageBinding.inflate(inflater, container, false);
        binding.emptyL.getRoot().setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(UserResultPageType.class.getCanonicalName())) {
            type = (UserResultPageType) args.getSerializable(UserResultPageType.class.getCanonicalName());
        }
    }
}