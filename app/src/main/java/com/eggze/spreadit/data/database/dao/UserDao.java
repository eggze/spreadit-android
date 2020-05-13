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
package com.eggze.spreadit.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.eggze.spreadit.data.database.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
@Dao
public abstract class UserDao extends BaseDao<User>  {
    public final static String ID = "id";
    public final static String UUID = "uuid";
    public final static String POSITIVES = "positives";
    public final static String POSITIVE_DATE = "positive_date";
    public final static String CONTACTS = "contacts";
    public final static String LAST_UPDATE = "last_update";
    public final static String VERSION = "version";

    public final static String TABLE = "user";

    @Query("DELETE FROM " + TABLE)
    public abstract void deleteAll();

    @Query("SELECT * FROM " + TABLE + " WHERE ID = :id")
    public abstract LiveData<User> fetchById(int id);

    @Query("SELECT * FROM " + TABLE + " WHERE UUID = :uuid")
    public abstract LiveData<User> fetchByUUID(String uuid);

    @Query("SELECT * FROM " + TABLE + " WHERE UUID = :uuid")
    public abstract User getByUUID(String uuid);

    @Query("SELECT * FROM " + TABLE + " ORDER BY ID LIMIT 1")
    public abstract User getActiveUser();

    @Query("SELECT * FROM " + TABLE + " ORDER BY ID LIMIT 1")
    public abstract LiveData<User> fetchActiveUser();

    @Query("SELECT * FROM " + TABLE)
    public abstract LiveData<List<User>> fetchAll();

    @Query("SELECT * FROM " + TABLE)
    public abstract List<User> getAll();

    @Query("SELECT * FROM " + TABLE + " WHERE ID = :id")
    public abstract User getById(int id);

    @Query("DELETE FROM " + TABLE + " WHERE ID IN (:data)")
    public abstract void deleteIn(List<Integer> data);

    @Query("DELETE FROM " + TABLE + " WHERE ID = :id")
    public abstract void delete(int id);

    @Transaction
    public void upsert(User data) {
        long id = insert(data);
        if (id == -1L) {
            update(data);
        }
    }

    @Transaction
    public void upsert(List<User> dataList) {
            List<Long> insertResult = insertAll(dataList);
            ArrayList<User> updateList = new ArrayList<>();
            for (int i= 0; i < insertResult.size(); i++) {
                if (insertResult.get(i) == -1L) {
                    updateList.add(dataList.get(i));
                }
            }
            if (!updateList.isEmpty()) {
                updateAll(dataList);
            }
    }
}
