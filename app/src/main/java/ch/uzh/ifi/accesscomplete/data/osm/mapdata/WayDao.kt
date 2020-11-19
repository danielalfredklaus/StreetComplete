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
