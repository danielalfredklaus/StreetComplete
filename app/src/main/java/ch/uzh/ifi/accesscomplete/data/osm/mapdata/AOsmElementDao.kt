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

import android.database.sqlite.SQLiteOpenHelper

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.ObjectRelationalMapping
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestTable
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable
import ch.uzh.ifi.accesscomplete.ktx.queryOne
import ch.uzh.ifi.accesscomplete.ktx.transaction

/** Abstract base class for the DAOs that store the OSM elements */
abstract class AOsmElementDao<T : Element>(private val dbHelper: SQLiteOpenHelper) {

    private val db get() = dbHelper.writableDatabase

    protected abstract val elementTypeName: String
    protected abstract val tableName: String
    protected abstract val idColumnName: String
    protected abstract val mapping: ObjectRelationalMapping<T>

    fun putAll(elements: Collection<T>) {
        db.transaction {
            for (element in elements) {
                put(element)
            }
        }
    }

    fun put(element: T) {
        db.replaceOrThrow(tableName, null, mapping.toContentValues(element))
    }

    fun delete(id: Long) {
        db.delete(tableName, "$idColumnName = $id", null)
    }

    fun get(id: Long): T? {
        return db.queryOne(tableName, null, "$idColumnName = $id", null) { mapping.toObject(it) }
    }

    /** Cleans up element entries that are not referenced by any quest anymore.  */
    open fun deleteUnreferenced() {
        val where = """
            $idColumnName NOT IN (
            ${getSelectAllElementIdsIn(OsmQuestTable.NAME)}
            UNION
            ${getSelectAllElementIdsIn(UndoOsmQuestTable.NAME)}
            )""".trimIndent()

        db.delete(tableName, where, null)
    }

    protected fun getSelectAllElementIdsIn(table: String) = """
        SELECT ${OsmQuestTable.Columns.ELEMENT_ID} AS $idColumnName
        FROM $table
        WHERE ${OsmQuestTable.Columns.ELEMENT_TYPE} = "$elementTypeName"
    """
}
