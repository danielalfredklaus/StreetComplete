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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo

import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometryMapping
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.Columns.QUEST_ID
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.Columns.QUEST_TYPE
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.Columns.ELEMENT_ID
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.Columns.ELEMENT_TYPE
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.Columns.TAG_CHANGES
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.Columns.CHANGES_SOURCE
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.NAME
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable.NAME_MERGED_VIEW
import ch.uzh.ifi.accesscomplete.ktx.*
import ch.uzh.ifi.accesscomplete.util.Serializer
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Singleton

/** Stores UndoOsmQuest objects - to reverse a previously already uploaded change through OsmQuest */
@Singleton class UndoOsmQuestDao @Inject constructor(
    private val dbHelper: SQLiteOpenHelper,
    private val mapping: UndoOsmQuestMapping
) {
    /* Must be a singleton because there is a listener that should respond to a change in the
     *  database table */

    private val db get() = dbHelper.writableDatabase

    interface Listener {
        fun onAddedUndoOsmQuest()
        fun onDeletedUndoOsmQuest()
    }

    private val listeners: MutableList<Listener> = CopyOnWriteArrayList()

    fun getAll(): List<UndoOsmQuest> {
        return db.query(NAME_MERGED_VIEW) { mapping.toObject(it) }
    }

    fun get(questId: Long): UndoOsmQuest? {
        val selection = "$QUEST_ID = ?"
        val args = arrayOf(questId.toString())
        return db.queryOne(NAME_MERGED_VIEW, null, selection, args) { mapping.toObject(it) }
    }

    fun getCount(): Int {
        return db.queryOne(NAME, arrayOf("COUNT(*)")) { it.getInt(0) } ?: 0
    }

    fun delete(questId: Long): Boolean {
        val result = db.delete(NAME, "$QUEST_ID = ?", arrayOf(questId.toString())) == 1
        if (result) listeners.forEach { it.onDeletedUndoOsmQuest() }
        return result
    }

    fun add(quest: UndoOsmQuest) {
        db.insertOrThrow(NAME, null, mapping.toContentValues(quest))
        listeners.forEach { it.onAddedUndoOsmQuest() }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
}

class UndoOsmQuestMapping @Inject constructor(
        private val serializer: Serializer,
        private val questTypeList: QuestTypeRegistry,
        private val elementGeometryMapping: ElementGeometryMapping
) : ObjectRelationalMapping<UndoOsmQuest> {

    override fun toContentValues(obj: UndoOsmQuest) = contentValuesOf(
        QUEST_ID to obj.id,
        QUEST_TYPE to obj.type.javaClass.simpleName,
        TAG_CHANGES to serializer.toBytes(obj.changes),
        CHANGES_SOURCE to obj.changesSource,
        ELEMENT_TYPE to obj.elementType.name,
        ELEMENT_ID to obj.elementId
    )

    override fun toObject(cursor: Cursor) = UndoOsmQuest(
            cursor.getLong(QUEST_ID),
            questTypeList.getByName(cursor.getString(QUEST_TYPE)) as OsmElementQuestType<*>,
            Element.Type.valueOf(cursor.getString(ELEMENT_TYPE)),
            cursor.getLong(ELEMENT_ID),
            serializer.toObject(cursor.getBlob(TAG_CHANGES)),
            cursor.getString(CHANGES_SOURCE),
            elementGeometryMapping.toObject(cursor)
    )
}
