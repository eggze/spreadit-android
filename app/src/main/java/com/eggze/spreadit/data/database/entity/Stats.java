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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.data.BaseModel;
import com.eggze.spreadit.data.database.dao.StatsDao;

import java.util.Date;

@Entity(tableName = StatsDao.TABLE)

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class Stats extends BaseModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = StatsDao.ID)
    private long id;

    @ColumnInfo(name = StatsDao.TIMESTAMP)
    private long timestamp;
    @ColumnInfo(name = StatsDao.POSITIVES)
    private long positives;
    @ColumnInfo(name = StatsDao.TESTED)
    private long testedUsers;
    @ColumnInfo(name = StatsDao.TESTS)
    private long tests;
    @ColumnInfo(name = StatsDao.LOCATIONS)
    private long locations;
    @ColumnInfo(name = StatsDao.SCANS)
    private long scans;
    @ColumnInfo(name = StatsDao.USERS)
    private long users;
    @ColumnInfo(name = StatsDao.CONTACTS)
    private long contacts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getPositives() {
        return positives;
    }

    public void setPositives(long positives) {
        this.positives = positives;
    }

    public long getTestedUsers() {
        return testedUsers;
    }

    public void setTestedUsers(long testedUsers) {
        this.testedUsers = testedUsers;
    }

    public long getTests() {
        return tests;
    }

    public void setTests(long tests) {
        this.tests = tests;
    }

    public long getLocations() {
        return locations;
    }

    public void setLocations(long locations) {
        this.locations = locations;
    }

    public long getScans() {
        return scans;
    }

    public void setScans(long scans) {
        this.scans = scans;
    }

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    public long getContacts() {
        return contacts;
    }

    public void setContacts(long contacts) {
        this.contacts = contacts;
    }

    public String getFTimestamp() {
        try {
            return Spreadit.dateTimeFormat.format(new Date(timestamp));
        }catch (Exception e){
            return "";
        }
    }

    public String getFPositives() {
        try {
            return Spreadit.numberFormat.format(positives);
        }catch (Exception e){
            return String.valueOf(positives);
        }
    }


    public String getFTestedUsers() {

        try {
            return Spreadit.numberFormat.format(testedUsers);
        }catch (Exception e){
            return String.valueOf(testedUsers);
        }
    }


    public String getFTests() {

        try {
            return Spreadit.numberFormat.format(tests);
        }catch (Exception e){
            return String.valueOf(tests);
        }
    }


    public String getFLocations() {
        try {
            return Spreadit.numberFormat.format(locations);
        }catch (Exception e){
            return String.valueOf(locations);
        }
    }


    public String getFScans() {
        try {
            return Spreadit.numberFormat.format(scans);
        }catch (Exception e){
            return String.valueOf(scans);
        }
    }


    public String getFUsers() {
        try {
            return Spreadit.numberFormat.format(users);
        }catch (Exception e){
            return String.valueOf(users);
        }
    }


    public String getFContacts() {
        try {
            return Spreadit.numberFormat.format(contacts);
        }catch (Exception e){
            return String.valueOf(contacts);
        }
    }
}
