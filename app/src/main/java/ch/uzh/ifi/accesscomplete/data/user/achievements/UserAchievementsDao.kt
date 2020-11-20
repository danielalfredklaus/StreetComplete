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

package ch.uzh.ifi.accesscomplete.data.user.achievements

import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserAchievementsTable.Columns.ACHIEVEMENT
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserAchievementsTable.Columns.LEVEL
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserAchievementsTable.NAME

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.ktx.*

/** Stores which achievement ids have been unlocked by the user and at which level */
class UserAchievementsDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {
    private val db get() = dbHelper.writableDatabase

    fun getAll(): Map<String, Int> {
        return db.query(NAME) {
            it.getString(ACHIEVEMENT) to it.getInt(LEVEL)
        }.toMap()
    }

    fun clear() {
        db.delete(NAME, null, null)
    }

    fun put(achievement: String, level: Int) {
        db.insertWithOnConflict(NAME, null, contentValuesOf(
            ACHIEVEMENT to achievement,
            LEVEL to level
        ), CONFLICT_REPLACE)
    }
}
