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

import com.eggze.spreadit.common.packet.server.results.SendResultsPacketIF;
import com.eggze.spreadit.data.database.entity.UserResult;
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
public abstract class UserResultDao extends BaseDao<UserResult> {
    public final static String ID = "id";
    public final static String UUID = "uuid";
    public final static String SERIAL = "serial";
    public final static String TEST_RESULT = "result_status";
    public final static String SCAN_TIMESTAMP = "scan_timestamp";
    public final static String RESULT_TIMESTAMP = "result_timestamp";
    public final static String STATUS = "status";

    public final static String TABLE = "user_result";

    @Query("DELETE FROM " + TABLE)
    public abstract void deleteAll();

    @Query("UPDATE " + TABLE + " SET STATUS = :status WHERE ID = :id")
    public abstract void updateStatus(long id, int status);

    @Query("UPDATE " + TABLE + " SET " + TEST_RESULT + " = :result, " + RESULT_TIMESTAMP + " = :resultTimestamp WHERE " + UUID + " = :uuid")
    public abstract void updateResult(byte[] uuid, int result, long resultTimestamp);

    @Query("UPDATE " + TABLE + " SET " + TEST_RESULT + " = :result, " + RESULT_TIMESTAMP + " = :resultTimestamp WHERE " + ID + " = :id")
    public abstract void updateResultWithId(long id, int result, long resultTimestamp);

    @Query("SELECT * FROM " + TABLE + " WHERE ID = :id")
    public abstract LiveData<UserResult> fetchById(int id);

    @Query("SELECT COUNT(*) FROM " + TABLE + " WHERE " + TEST_RESULT + " = " + SendResultsPacketIF.RES_PENDING)
    public abstract int getResultPendingCount();

    @Query("SELECT COUNT(*) FROM " + TABLE + " WHERE " + TEST_RESULT + " != " + SendResultsPacketIF.RES_PENDING + " AND " + TEST_RESULT + " != " + SendResultsPacketIF.RES_INVALID)
    public abstract int getWithResultsCount();

    @Query("SELECT COUNT(*) FROM " + TABLE + " WHERE " + TEST_RESULT + " = " + SendResultsPacketIF.RES_POSITIVE + " AND " + RESULT_TIMESTAMP + " > :timestamp")
    public abstract LiveData<Integer> fetchPositiveCount(long timestamp);

    @Query("SELECT COUNT(*) FROM " + TABLE)
    public abstract int fetchCount();

    @Query("SELECT * FROM " + TABLE + " ORDER BY SCAN_TIMESTAMP DESC LIMIT 1")
    public abstract UserResult getLast();

    @Query("SELECT * FROM " + TABLE + " ORDER BY SCAN_TIMESTAMP DESC LIMIT 1")
    public abstract LiveData<UserResult> fetchLast();

    @Query("SELECT * FROM " + TABLE + " WHERE " + TEST_RESULT + " = " + SendResultsPacketIF.RES_PENDING
            + " ORDER BY SCAN_TIMESTAMP ASC")
    public abstract LiveData<List<UserResult>> fetchResultPending();

    @Query("SELECT * FROM " + TABLE + " WHERE " + TEST_RESULT + " = " + SendResultsPacketIF.RES_PENDING
            + " ORDER BY SCAN_TIMESTAMP ASC")
    public abstract List<UserResult> getResultPending();

    @Query("SELECT * FROM " + TABLE + " WHERE " + TEST_RESULT + " != " + SendResultsPacketIF.RES_PENDING
            + " ORDER BY RESULT_TIMESTAMP ASC")
    public abstract LiveData<List<UserResult>> fetchWithResults();

    @Query("SELECT * FROM " + TABLE + " WHERE " + TEST_RESULT + " != " + SendResultsPacketIF.RES_PENDING
            + " ORDER BY RESULT_TIMESTAMP ASC")
    public abstract List<UserResult> gethWithResults();

    @Query("SELECT ID FROM " + TABLE + " WHERE STATUS = " + ResponseHandler.STATUS_PENDING)
    public abstract List<Long> getPending();

    @Query("SELECT * FROM " + TABLE + " WHERE ID = :id")
    public abstract UserResult getById(long id);

    @Query("DELETE FROM " + TABLE + " WHERE ID IN (:data)")
    public abstract void deleteIn(List<Integer> data);

    @Query("DELETE FROM " + TABLE + " WHERE ID = :id")
    public abstract void delete(int id);

    @Transaction
    public void upsert(UserResult data) {
        long id = insert(data);
        if (id == -1L) {
            update(data);
        }
    }

    @Transaction
    public void upsert(List<UserResult> dataList) {
        List<Long> insertResult = insertAll(dataList);
        ArrayList<UserResult> updateList = new ArrayList<>();
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
