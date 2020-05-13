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
package com.eggze.spreadit.di;

import android.content.Context;

import com.eggze.spreadit.data.database.SpreaditDatabase;
import com.eggze.spreadit.data.database.dao.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
@Module
public class DatabaseModule {
    private Context context;

    public DatabaseModule(Context context) {
        this.context = context;
    }

    @Provides
    @Reusable
    public SpreaditDatabase provideDatabase() {
        return SpreaditDatabase.getInstance(context);
    }

    @Singleton
    @Provides
    public UserDao providesUserDao (SpreaditDatabase database) {
        return database.userDao();
    }

}