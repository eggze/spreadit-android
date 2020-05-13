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
import androidx.room.PrimaryKey;

import com.eggze.spreadit.common.util.UUIDUtil;
import com.eggze.spreadit.data.BaseModel;
import com.eggze.spreadit.data.database.dao.UserDao;

import java.util.UUID;

@Entity(tableName = UserDao.TABLE)
/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class User extends BaseModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = UserDao.ID)
    private long id;

    @NonNull
    @ColumnInfo(name = UserDao.UUID ,typeAffinity = ColumnInfo.BLOB)
    private byte[] uuid;

    @NonNull
    @ColumnInfo(name = UserDao.POSITIVES)
    private int positives;

    @NonNull
    @ColumnInfo(name = UserDao.CONTACTS)
    private int contactsCount;

    @NonNull
    @ColumnInfo(name = UserDao.VERSION)
    private int version;

    @ColumnInfo(name = UserDao.POSITIVE_DATE)
    private long positiveDate;

    @ColumnInfo(name = UserDao.LAST_UPDATE)
    private long lastUpdate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return UUIDUtil.toUUID(uuid).toString();
    }

    public void setUUID(UUID uuid) {
        this.uuid = UUIDUtil.toBytes(uuid);
    }

    public int getPositives() {
        return positives;
    }

    public void setPositives(int positives) {
        this.positives = positives;
    }

    public int getContactsCount() {
        return contactsCount;
    }

    public void setContactsCount(int contacts) {
        contactsCount = contacts;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getPositiveDate() {
        return positiveDate;
    }

    public void setPositiveDate(long positiveDate) {
        this.positiveDate = positiveDate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
