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
