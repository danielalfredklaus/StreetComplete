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

package ch.uzh.ifi.accesscomplete.data.osm.upload.changesets

import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenChangesetsTable.Columns.CHANGESET_ID
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenChangesetsTable.Columns.QUEST_TYPE
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenChangesetsTable.Columns.SOURCE
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenChangesetsTable.NAME
import ch.uzh.ifi.accesscomplete.ktx.getLong
import ch.uzh.ifi.accesscomplete.ktx.getString
import ch.uzh.ifi.accesscomplete.ktx.query
import ch.uzh.ifi.accesscomplete.ktx.queryOne

/** Keep track of changesets and the date of the last change that has been made to them  */
class OpenChangesetsDao @Inject constructor(
    private val dbHelper: SQLiteOpenHelper,
    private val mapping: OpenChangesetMapping
) {
    private val db get() = dbHelper.writableDatabase

    fun getAll(): Collection<OpenChangeset> {
        return db.query(NAME) { mapping.toObject(it) }
    }

    fun put(openChangeset: OpenChangeset) {
        db.replaceOrThrow(NAME, null, mapping.toContentValues(openChangeset))
    }

    fun get(questType: String, source: String): OpenChangeset? {
        val where = "$QUEST_TYPE = ? AND $SOURCE = ?"
        val args = arrayOf(questType, source)
        return db.queryOne(NAME, null, where, args) { mapping.toObject(it) }
    }

    fun delete(questType: String, source: String): Boolean {
        val where = "$QUEST_TYPE = ? AND $SOURCE = ?"
        val whereArgs = arrayOf(questType, source)
        return db.delete(NAME, where, whereArgs) == 1
    }
}

class OpenChangesetMapping @Inject constructor(): ObjectRelationalMapping<OpenChangeset> {

    override fun toContentValues(obj: OpenChangeset) = contentValuesOf(
        QUEST_TYPE to obj.questType,
        SOURCE to obj.source,
        CHANGESET_ID to obj.changesetId
    )

    override fun toObject(cursor: Cursor) = OpenChangeset(
            cursor.getString(QUEST_TYPE),
            cursor.getString(SOURCE),
            cursor.getLong(CHANGESET_ID)
    )
}
