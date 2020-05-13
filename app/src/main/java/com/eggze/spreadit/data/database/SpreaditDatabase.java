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
package com.eggze.spreadit.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.eggze.spreadit.data.database.dao.ScanLocationDao;
import com.eggze.spreadit.data.database.dao.StatsDao;
import com.eggze.spreadit.data.database.dao.UserDao;
import com.eggze.spreadit.data.database.dao.UserResultDao;
import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.data.database.entity.Stats;
import com.eggze.spreadit.data.database.entity.User;
import com.eggze.spreadit.data.database.entity.UserResult;

@Database(
        entities = {User.class, UserResult.class, ScanLocation.class, Stats.class}, version = 1, exportSchema = false
)

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public abstract class SpreaditDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract StatsDao statsDao();
    public abstract UserResultDao userResultDao();
    public abstract ScanLocationDao scanLocationDao();

    public volatile static SpreaditDatabase instance = null;

    public static SpreaditDatabase getInstance(Context context) {
        if (instance == null) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    private static SpreaditDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, SpreaditDatabase.class, "spreadit.db").build();
    }
}
