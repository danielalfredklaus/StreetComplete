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

import java.util.HashMap

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.util.Serializer
import de.westnordost.osmapi.map.data.Node
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmNode
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable.Columns.ID
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable.Columns.LATITUDE
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable.Columns.LONGITUDE
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable.Columns.TAGS
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable.Columns.VERSION
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable.NAME
import ch.uzh.ifi.accesscomplete.ktx.*

/** Stores OSM nodes */
class NodeDao @Inject constructor(dbHelper: SQLiteOpenHelper, override val mapping: NodeMapping)
    : AOsmElementDao<Node>(dbHelper) {

    override val tableName = NAME
    override val idColumnName = ID
    override val elementTypeName = Element.Type.NODE.name
}

class NodeMapping @Inject constructor(private val serializer: Serializer)
    : ObjectRelationalMapping<Node> {

    override fun toContentValues(obj: Node) = contentValuesOf(
        ID to obj.id,
        VERSION to obj.version,
        LATITUDE to obj.position.latitude,
        LONGITUDE to obj.position.longitude,
        TAGS to obj.tags?.let { serializer.toBytes(HashMap(it)) }
    )

    override fun toObject(cursor: Cursor) = OsmNode(
        cursor.getLong(ID),
        cursor.getInt(VERSION),
        OsmLatLon(cursor.getDouble(LATITUDE), cursor.getDouble(LONGITUDE)),
        cursor.getBlobOrNull(TAGS)?.let { serializer.toObject<HashMap<String, String>>(it) }
    )
}
