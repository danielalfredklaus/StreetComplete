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
import de.westnordost.osmapi.map.data.*
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping

import java.util.ArrayList
import java.util.HashMap

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.util.Serializer
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.RelationTable.Columns.ID
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.RelationTable.Columns.MEMBERS
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.RelationTable.Columns.TAGS
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.RelationTable.Columns.VERSION
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.RelationTable.NAME
import ch.uzh.ifi.accesscomplete.ktx.*

/** Stores OSM relations */
class RelationDao @Inject constructor(dbHelper: SQLiteOpenHelper, override val mapping: RelationMapping)
    : AOsmElementDao<Relation>(dbHelper) {

    override val tableName = NAME
    override val idColumnName = ID
    override val elementTypeName = Element.Type.RELATION.name
}

class RelationMapping @Inject constructor(private val serializer: Serializer)
    : ObjectRelationalMapping<Relation> {

    override fun toContentValues(obj: Relation) = contentValuesOf(
        ID to obj.id,
        VERSION to obj.version,
        MEMBERS to serializer.toBytes(ArrayList(obj.members)),
        TAGS to obj.tags?.let { serializer.toBytes(HashMap(it)) }
    )

    override fun toObject(cursor: Cursor) = OsmRelation(
        cursor.getLong(ID),
        cursor.getInt(VERSION),
        serializer.toObject<ArrayList<OsmRelationMember>>(cursor.getBlob(MEMBERS)) as List<RelationMember>,
        cursor.getBlobOrNull(TAGS)?.let { serializer.toObject<HashMap<String, String>>(it) }
    )
}
