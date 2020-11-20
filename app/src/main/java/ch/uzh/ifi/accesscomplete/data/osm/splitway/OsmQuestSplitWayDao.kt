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

package ch.uzh.ifi.accesscomplete.data.osm.splitway

import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.NAME
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.Columns.QUEST_ID
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.Columns.QUEST_TYPE
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.Columns.QUEST_TYPES_ON_WAY
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.Columns.SOURCE
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.Columns.SPLITS
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable.Columns.WAY_ID
import ch.uzh.ifi.accesscomplete.ktx.*
import ch.uzh.ifi.accesscomplete.util.Serializer
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

/** Stores OsmQuestSplitWay objects by quest ID - the solutions of "differs along the way" quest
 *  answers. */
@Singleton class OsmQuestSplitWayDao @Inject constructor(
    private val dbHelper: SQLiteOpenHelper,
    private val mapping: OsmQuestSplitWayMapping
) {
    /* Must be a singleton because there is a listener that should respond to a change in the
     *  database table */

    private val db get() = dbHelper.writableDatabase

    interface Listener {
        fun onAddedSplitWay()
        fun onDeletedSplitWay()
    }

    private val listeners: MutableList<Listener> = CopyOnWriteArrayList()

    fun getAll(): List<OsmQuestSplitWay> {
        return db.query(NAME) { mapping.toObject(it) }
    }

    fun get(questId: Long): OsmQuestSplitWay? {
        val selection = "$QUEST_ID = ?"
        val args = arrayOf(questId.toString())
        return db.queryOne(NAME, null, selection, args) { mapping.toObject(it) }
    }

    fun getCount(): Int {
        return db.queryOne(NAME, arrayOf("COUNT(*)")) { it.getInt(0) } ?: 0
    }

    fun add(quest: OsmQuestSplitWay) {
        db.insertOrThrow(NAME, null, mapping.toContentValues(quest))
        listeners.forEach { it.onAddedSplitWay() }
    }

    fun delete(questId: Long): Boolean {
        val result = db.delete(NAME, "$QUEST_ID = ?", arrayOf(questId.toString())) == 1
        if (result) listeners.forEach { it.onDeletedSplitWay() }
        return result
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
}

class OsmQuestSplitWayMapping @Inject constructor(
    private val serializer: Serializer,
    private val questTypeList: QuestTypeRegistry
) : ObjectRelationalMapping<OsmQuestSplitWay> {

    override fun toContentValues(obj: OsmQuestSplitWay) = contentValuesOf(
        QUEST_ID to obj.questId,
        QUEST_TYPE to obj.questType.javaClass.simpleName,
        WAY_ID to obj.wayId,
        SOURCE to obj.source,
        SPLITS to serializer.toBytes(ArrayList(obj.splits)),
        QUEST_TYPES_ON_WAY to obj.questTypesOnWay.joinToString(",") { it.javaClass.simpleName }
    )

    override fun toObject(cursor: Cursor)= OsmQuestSplitWay(
            cursor.getLong(QUEST_ID),
            questTypeList.getByName(cursor.getString(QUEST_TYPE)) as OsmElementQuestType<*>,
            cursor.getLong(WAY_ID),
            cursor.getString(SOURCE),
            (serializer.toObject(cursor.getBlob(SPLITS)) as ArrayList<SplitPolylineAtPosition>),
            cursor.getStringOrNull(QUEST_TYPES_ON_WAY)
                ?.split(',')
                ?.mapNotNull { questTypeList.getByName(it) as? OsmElementQuestType<*> }
                ?: emptyList()
    )
}
