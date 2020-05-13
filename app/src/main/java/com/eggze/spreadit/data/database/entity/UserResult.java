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
package com.eggze.spreadit.data.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.eggze.spreadit.common.util.UUIDUtil;
import com.eggze.spreadit.data.BaseModel;
import com.eggze.spreadit.data.database.dao.UserResultDao;
import com.eggze.spreadit.tcp.ResponseHandler;

import java.util.UUID;

@Entity(tableName = UserResultDao.TABLE, indices = @Index(value = {UserResultDao.UUID}, unique = true))

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserResult extends BaseModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = UserResultDao.ID)
    private long id;

    @NonNull
    @ColumnInfo(name = UserResultDao.UUID ,typeAffinity = ColumnInfo.BLOB)
    private byte[] uuid;

    @NonNull
    @ColumnInfo(name = UserResultDao.SERIAL)
    private int serial;

    @NonNull
    @ColumnInfo(name = UserResultDao.TEST_RESULT)
    private int testResult;

    @NonNull
    @ColumnInfo(name = UserResultDao.SCAN_TIMESTAMP)
    private long scanTimestamp;

    @NonNull
    @ColumnInfo(name = UserResultDao.RESULT_TIMESTAMP)
    private long resultTimestamp;

    @NonNull
    @ColumnInfo(name = UserResultDao.STATUS)
    private int status = ResponseHandler.STATUS_PENDING;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUUID() {
        return UUIDUtil.toUUID(uuid).toString();
    }

    public void setUUID(UUID uuid) {
        this.uuid = UUIDUtil.toBytes(uuid);
    }

    public int getTestResult() {
        return testResult;
    }

    public void setTestResult(int testResult) {
        this.testResult = testResult;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public long getScanTimestamp() {
        return scanTimestamp;
    }

    public void setScanTimestamp(long scanTimestamp) {
        this.scanTimestamp = scanTimestamp;
    }

    public long getResultTimestamp() {
        return resultTimestamp;
    }

    public void setResultTimestamp(long resultTimestamp) {
        this.resultTimestamp = resultTimestamp;
    }

    @NonNull
    public int getSerial() {
        return serial;
    }

    public void setSerial(@NonNull int serial) {
        this.serial = serial;
    }


    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }
}
