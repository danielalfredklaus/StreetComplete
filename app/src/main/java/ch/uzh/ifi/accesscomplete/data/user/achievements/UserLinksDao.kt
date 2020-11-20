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

import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserLinksTable.Columns.LINK
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserLinksTable.NAME

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.ktx.*

/** Stores which link ids have been unlocked by the user */
class UserLinksDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {
    private val db get() = dbHelper.writableDatabase

    fun getAll(): List<String> {
        return db.query(NAME) { it.getString(LINK) }
    }

    fun clear() {
        db.delete(NAME, null, null)
    }

    fun add(link: String) {
        db.insertWithOnConflict(NAME, null, contentValuesOf(LINK to link), CONFLICT_IGNORE)
    }

    fun addAll(links: List<String>): Int {
        var addedRows = 0
        db.transaction {
            for (link in links) {
                val rowId = db.insertWithOnConflict(NAME, null, contentValuesOf(LINK to link), CONFLICT_IGNORE)
                if (rowId != -1L) addedRows++
            }
        }
        return addedRows
    }
}
