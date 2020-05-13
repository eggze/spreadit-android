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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.eggze.spreadit.data.enums.UserResultPageType;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserResultsAdapter extends FragmentStateAdapter {
    public UserResultsAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        UserResultsPageFragment fragment = null;
        switch (position){
            case 0: {
                fragment = UserResultsPageFragment.newInstance(UserResultPageType.PENDING);
                break;
            }
            case 1: {
                fragment = UserResultsPageFragment.newInstance(UserResultPageType.WITH_RESULTS);
                break;
            }
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
