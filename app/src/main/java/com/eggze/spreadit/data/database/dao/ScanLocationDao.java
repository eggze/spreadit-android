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

import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.tcp.ResponseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
@Dao
public abstract class ScanLocationDao extends BaseDao<ScanLocation> {
    public final static String ID = "id";
    public final static String UUID = "uuid";
    public final static String LOC_UUID = "loc_uuid";
    public final static String SCAN_TIMESTAMP = "scan_timestamp";
    public final static String STATUS = "status";
    public final static String RESULT = "result_status";
    public final static String LAT = "latitude";
    public final static String LON = "longitude";
    public final static String DATE_STATUS = "date_status";
    public final static String CONTACTS = "contacts";

    public final static String TABLE = "location_scan";

    @Query("DELETE FROM " + TABLE)
    public abstract void deleteAll();

    @Query("UPDATE " + TABLE + " SET STATUS = :status WHERE ID = :id")
    public abstract void updateStatus(long id, int status);

    @Query("SELECT * FROM " + TABLE + " ORDER BY SCAN_TIMESTAMP ASC")
    public abstract LiveData<ScanLocation> fetchAll();

    @Query("SELECT * FROM " + TABLE + " WHERE ID = :id")
    public abstract LiveData<ScanLocation> fetchById(int id);

    @Query("SELECT * FROM " + TABLE + " WHERE UUID = :uuid")
    public abstract LiveData<ScanLocation> fetchByUUID(String uuid);

    @Query("SELECT ID FROM " + TABLE + " WHERE STATUS = " + ResponseHandler.STATUS_PENDING)
    public abstract List<Long> getPending();

    @Query("SELECT * FROM " + TABLE + " WHERE " + UUID + " = :uuid")
    public abstract ScanLocation getByUUID(String uuid);

    @Query("SELECT COUNT(*) FROM " + TABLE)
    public abstract int getCount();

    @Query("SELECT * FROM " + TABLE + " ORDER BY SCAN_TIMESTAMP  DESC LIMIT 1")
    public abstract ScanLocation getLast();

    @Query("SELECT * FROM " + TABLE + " ORDER BY SCAN_TIMESTAMP DESC LIMIT 1")
    public abstract LiveData<ScanLocation> fetchLast();

    @Query("SELECT * FROM " + TABLE + " WHERE ID = :id")
    public abstract ScanLocation getById(long id);

    @Query("DELETE FROM " + TABLE + " WHERE ID IN (:data)")
    public abstract void deleteIn(List<Integer> data);

    @Query("DELETE FROM " + TABLE + " WHERE ID = :id")
    public abstract void delete(int id);

    @Query("UPDATE " + TABLE + " SET " + RESULT + " = 1 WHERE SCAN_TIMESTAMP = :timestamp")
    public abstract void updateResult(long timestamp);

    @Query("SELECT * FROM " + TABLE + " WHERE " + RESULT + " = 0" + " ORDER BY SCAN_TIMESTAMP DESC")
    public abstract LiveData<List<ScanLocation>> fetchResultPending();

    @Query("SELECT * FROM " + TABLE + " WHERE " + RESULT + " = 0" + " ORDER BY SCAN_TIMESTAMP DESC")
    public abstract List<ScanLocation> getResultPending();

    @Query("SELECT * FROM " + TABLE + " WHERE " + RESULT + " != 0" + " ORDER BY SCAN_TIMESTAMP DESC")
    public abstract LiveData<List<ScanLocation>> fetchWithResults();

    @Query("SELECT * FROM " + TABLE + " WHERE " + RESULT + " != 0" + " ORDER BY SCAN_TIMESTAMP DESC")
    public abstract List<ScanLocation> gethWithResults();

    @Transaction
    public void upsert(ScanLocation data) {
        long id = insert(data);
        if (id == -1L) {
            update(data);
        }
    }

    @Transaction
    public void upsert(List<ScanLocation> dataList) {
        List<Long> insertResult = insertAll(dataList);
        ArrayList<ScanLocation> updateList = new ArrayList<>();
        for (int i = 0; i < insertResult.size(); i++) {
            if (insertResult.get(i) == -1L) {
                updateList.add(dataList.get(i));
            }
        }
        if (!updateList.isEmpty()) {
            updateAll(dataList);
        }
    }
}
