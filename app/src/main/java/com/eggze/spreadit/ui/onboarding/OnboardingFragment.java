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
package com.eggze.spreadit.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eggze.spreadit.R;
import com.eggze.spreadit.activities.TermsActivity;
import com.eggze.spreadit.databinding.FragmentOnboardingBinding;
import com.eggze.spreadit.ui.main.BaseFragment;
import com.eggze.spreadit.util.OnboardingType;
import com.eggze.spreadit.util.Statics;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class OnboardingFragment extends BaseFragment {
    private FragmentOnboardingBinding binding;

    public static OnboardingFragment newInstance(Bundle bundle) {
        OnboardingFragment fragment = new OnboardingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        binding.setOnClickListener(this);
        handleArgs();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void handleArgs() {
        Bundle arguments = getArguments();
        binding.rootL.setBackgroundResource(arguments.getInt(Statics.ARG_ONBOARDING_COLOR, 0xFFFFFFFF));
        binding.titleTxtv.setText(arguments.getInt(Statics.ARG_ONBOARDING_TITLE));
        binding.subtitleTxtv.setText(arguments.getInt(Statics.ARG_ONBOARDING_INFO));
        if (arguments.getSerializable(Statics.ARG_ONBOARDING_TYPE) == OnboardingType.PRIVACY_POLICY) {
            binding.actionBtn.setVisibility(View.VISIBLE);
        } else {
            binding.actionBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_btn: {
                startActivity(new Intent(getActivity(), TermsActivity.class));
                break;
            }
        }
    }
}
