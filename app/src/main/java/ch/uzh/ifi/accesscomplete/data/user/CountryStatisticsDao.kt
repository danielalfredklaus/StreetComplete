/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.data.user

import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.user.CountryStatisticsTable.Columns.COUNTRY_CODE
import ch.uzh.ifi.accesscomplete.data.user.CountryStatisticsTable.Columns.RANK
import ch.uzh.ifi.accesscomplete.data.user.CountryStatisticsTable.Columns.SUCCEEDED
import ch.uzh.ifi.accesscomplete.data.user.CountryStatisticsTable.NAME

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.ktx.*
import javax.inject.Singleton

/** Stores how many quests the user solved in which country */
@Singleton class CountryStatisticsDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {
    private val db get() = dbHelper.writableDatabase

    fun getCountryWithBiggestSolvedCount(): CountryStatistics? {
        return db.queryOne(NAME, orderBy = "$SUCCEEDED DESC") {
            CountryStatistics(it.getString(COUNTRY_CODE), it.getInt(SUCCEEDED), it.getIntOrNull(RANK))
        }
    }

    fun getAll(): List<CountryStatistics> {
        return db.query(NAME) {
            CountryStatistics(it.getString(COUNTRY_CODE), it.getInt(SUCCEEDED), it.getIntOrNull(RANK))
        }
    }

    fun clear() {
        db.delete(NAME, null, null)
    }

    fun replaceAll(countriesStatistics: Collection<CountryStatistics>) {
        db.transaction {
            db.delete(NAME, null, null)
            for (statistics in countriesStatistics) {
                db.insert(NAME, null, contentValuesOf(
                    COUNTRY_CODE to statistics.countryCode,
                    SUCCEEDED to statistics.solvedCount,
                    RANK to statistics.rank
                ))
            }
        }
    }

    fun addOne(countryCode: String) {
        // first ensure the row exists
        db.insertWithOnConflict(NAME, null, contentValuesOf(
            COUNTRY_CODE to countryCode,
            SUCCEEDED to 0
        ), CONFLICT_IGNORE)

        // then increase by one
        db.execSQL("UPDATE $NAME SET $SUCCEEDED = $SUCCEEDED + 1 WHERE $COUNTRY_CODE = ?", arrayOf(countryCode))
    }

    fun subtractOne(countryCode: String) {
        db.execSQL("UPDATE $NAME SET $SUCCEEDED = $SUCCEEDED - 1 WHERE $COUNTRY_CODE = ?", arrayOf(countryCode))
    }
}
