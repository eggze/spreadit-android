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
import com.eggze.spreadit.data.database.dao.ScanLocationDao;
import com.eggze.spreadit.tcp.ResponseHandler;

import java.util.UUID;

@Entity(tableName = ScanLocationDao.TABLE, indices = @Index(value = {ScanLocationDao.UUID}, unique = true))
/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class ScanLocation extends BaseModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ScanLocationDao.ID)
    private long id;

    @NonNull
    @ColumnInfo(name = ScanLocationDao.UUID, typeAffinity = ColumnInfo.BLOB)
    private byte[] uuid;

    @NonNull
    @ColumnInfo(name = ScanLocationDao.LOC_UUID, typeAffinity = ColumnInfo.BLOB)
    private byte[] locationUuid;

    @ColumnInfo(name = ScanLocationDao.SCAN_TIMESTAMP)
    private long scanTimestamp = System.currentTimeMillis();

    @ColumnInfo(name = ScanLocationDao.STATUS)
    private int status = ResponseHandler.STATUS_PENDING;

    @ColumnInfo(name = ScanLocationDao.LAT)
    private double latitude;

    @ColumnInfo(name = ScanLocationDao.LON)
    private double longitude;

    @ColumnInfo(name = ScanLocationDao.DATE_STATUS)
    private int dateStatus;

    @ColumnInfo(name = ScanLocationDao.RESULT)
    private int scanStatus;

    @ColumnInfo(name = ScanLocationDao.CONTACTS)
    private int contacts;

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

    public long getScanTimestamp() {
        return scanTimestamp;
    }

    public void setScanTimestamp(long scanTimestamp) {
        this.scanTimestamp = scanTimestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @NonNull
    public String getLocationUUID() {
        return UUIDUtil.toUUID(locationUuid).toString();
    }

    public void setLocationUUID(@NonNull UUID locationUuid) {
        this.locationUuid = UUIDUtil.toBytes(locationUuid);
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }


    public byte[] getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(byte[] locationUuid) {
        this.locationUuid = locationUuid;
    }

    public int getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(int scanStatus) {
        this.scanStatus = scanStatus;
    }


    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }

    public int getContacts() {
        return contacts;
    }

    public void setContacts(int contacts) {
        this.contacts = contacts;
    }
}
