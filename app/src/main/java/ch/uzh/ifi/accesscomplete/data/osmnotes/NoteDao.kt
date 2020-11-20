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

package ch.uzh.ifi.accesscomplete.data.osmnotes


import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.LatLon

import java.util.ArrayList
import java.util.Date

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.util.Serializer
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.notes.Note
import de.westnordost.osmapi.notes.NoteComment
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping
import ch.uzh.ifi.accesscomplete.data.WhereSelectionBuilder
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.CLOSED
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.COMMENTS
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.CREATED
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.ID
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.LATITUDE
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.LONGITUDE
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.Columns.STATUS
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable.NAME
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestTable
import ch.uzh.ifi.accesscomplete.ktx.*

/** Stores OSM notes */
class NoteDao @Inject constructor(
    private val dbHelper: SQLiteOpenHelper,
    private val mapping: NoteMapping
) {
    private val db get() = dbHelper.writableDatabase

    fun putAll(notes: Collection<Note>) {
        db.transaction {
            for (note in notes) {
                put(note)
            }
        }
    }

    fun put(note: Note) {
        db.replaceOrThrow(NAME, null, mapping.toContentValues(note))
    }

    fun get(id: Long): Note? {
        return db.queryOne(NAME, null, "$ID = $id") { mapping.toObject(it) }
    }

    fun delete(id: Long): Boolean {
        return db.delete(NAME, "$ID = $id", null) == 1
    }

    fun getAllPositions(bbox: BoundingBox): List<LatLon> {
        val cols = arrayOf(LATITUDE, LONGITUDE)
        val builder = WhereSelectionBuilder()
        builder.appendBounds(bbox)
        return db.query(NAME, cols, builder.where, builder.args) { OsmLatLon(it.getDouble(0), it.getDouble(1)) }
    }

    fun deleteUnreferenced(): Int {
        val where = ID + " NOT IN ( " +
                "SELECT " + OsmNoteQuestTable.Columns.NOTE_ID + " FROM " + OsmNoteQuestTable.NAME +
                ")"

        return db.delete(NAME, where, null)
    }
}

private fun WhereSelectionBuilder.appendBounds(bbox: BoundingBox) {
    add("($LATITUDE BETWEEN ? AND ?)",
        bbox.minLatitude.toString(),
        bbox.maxLatitude.toString()
    )
    add(
        "($LONGITUDE BETWEEN ? AND ?)",
        bbox.minLongitude.toString(),
        bbox.maxLongitude.toString()
    )
}

class NoteMapping @Inject constructor(private val serializer: Serializer)
    : ObjectRelationalMapping<Note> {

    override fun toContentValues(obj: Note) = contentValuesOf(
        ID to obj.id,
        LATITUDE to obj.position.latitude,
        LONGITUDE to obj.position.longitude,
        STATUS to obj.status.name,
        CREATED to obj.dateCreated.time,
        CLOSED to obj.dateClosed?.time,
        COMMENTS to serializer.toBytes(ArrayList(obj.comments))
    )

    override fun toObject(cursor: Cursor) = Note().also { n ->
        n.id = cursor.getLong(ID)
        n.position = OsmLatLon(cursor.getDouble(LATITUDE), cursor.getDouble(LONGITUDE))
        n.dateCreated = Date(cursor.getLong(CREATED))
        n.dateClosed = cursor.getLongOrNull(CLOSED)?.let { Date(it) }
        n.status = Note.Status.valueOf(cursor.getString(STATUS))
        n.comments = serializer.toObject<ArrayList<NoteComment>>(cursor.getBlob(COMMENTS))
    }
}
