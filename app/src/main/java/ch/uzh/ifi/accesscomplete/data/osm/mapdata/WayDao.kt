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

package ch.uzh.ifi.accesscomplete.data.osm.mapdata

import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import de.westnordost.osmapi.map.data.Element

import java.util.ArrayList
import java.util.HashMap

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.util.Serializer
import de.westnordost.osmapi.map.data.OsmWay
import de.westnordost.osmapi.map.data.Way
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestTable
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.WayTable.Columns.ID
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.WayTable.Columns.NODE_IDS
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.WayTable.Columns.TAGS
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.WayTable.Columns.VERSION
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable
import ch.uzh.ifi.accesscomplete.ktx.*

/** Stores OSM ways */
class WayDao @Inject constructor(private val dbHelper: SQLiteOpenHelper, override val mapping: WayMapping)
    : AOsmElementDao<Way>(dbHelper) {

    private val db get() = dbHelper.writableDatabase

    override val tableName = WayTable.NAME
    override val idColumnName = ID
    override val elementTypeName = Element.Type.WAY.name

    /** Cleans up element entries that are not referenced by any quest anymore.  */
    override fun deleteUnreferenced() {
        val where = """
            $idColumnName NOT IN (
            ${getSelectAllElementIdsIn(OsmQuestTable.NAME)}
            UNION
            ${getSelectAllElementIdsIn(UndoOsmQuestTable.NAME)}
            UNION
            SELECT ${OsmQuestSplitWayTable.Columns.WAY_ID} AS $idColumnName FROM ${OsmQuestSplitWayTable.NAME}
            )""".trimIndent()

        db.delete(tableName, where, null)
    }
}

class WayMapping @Inject constructor(private val serializer: Serializer)
    : ObjectRelationalMapping<Way> {

    override fun toContentValues(obj: Way) = contentValuesOf(
        ID to obj.id,
        VERSION to obj.version,
        NODE_IDS to serializer.toBytes(ArrayList(obj.nodeIds)),
        TAGS to obj.tags?.let { serializer.toBytes(HashMap(it)) }
    )

    override fun toObject(cursor: Cursor) = OsmWay(
        cursor.getLong(ID),
        cursor.getInt(VERSION),
        serializer.toObject<ArrayList<Long>>(cursor.getBlob(NODE_IDS)),
        cursor.getBlobOrNull(TAGS)?.let { serializer.toObject<HashMap<String, String>>(it) }
    )
}
