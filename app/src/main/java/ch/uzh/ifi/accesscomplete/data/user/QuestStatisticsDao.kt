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

package ch.uzh.ifi.accesscomplete.data.user

import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.user.QuestStatisticsTable.Columns.QUEST_TYPE
import ch.uzh.ifi.accesscomplete.data.user.QuestStatisticsTable.Columns.SUCCEEDED
import ch.uzh.ifi.accesscomplete.data.user.QuestStatisticsTable.NAME
import ch.uzh.ifi.accesscomplete.ktx.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Singleton

/** Stores how many quests of which quest types the user solved */
@Singleton class QuestStatisticsDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {
    private val db get() = dbHelper.writableDatabase

    interface Listener {
        fun onAddedOne(questType: String)
        fun onSubtractedOne(questType: String)
        fun onReplacedAll()
    }

    private val listeners: MutableList<Listener> = CopyOnWriteArrayList()

    fun getTotalAmount(): Int {
        return db.queryOne(NAME, arrayOf("total($SUCCEEDED)")) { it.getInt(0) } ?: 0
    }

    fun getAll(): Map<String, Int> {
        return db.query(NAME) {
            it.getString(QUEST_TYPE) to it.getInt(SUCCEEDED)
        }.toMap()
    }

    fun clear() {
        db.delete(NAME, null, null)
        listeners.forEach { it.onReplacedAll() }
    }

    fun replaceAll(amounts: Map<String, Int>) {
        db.transaction {
            db.delete(NAME, null, null)
            for ((key, value) in amounts) {
                db.insert(NAME, null, contentValuesOf(
                    QUEST_TYPE to key,
                    SUCCEEDED to value
                ))
            }
        }
        listeners.forEach { it.onReplacedAll() }
    }

    fun addOne(questType: String) {
        // first ensure the row exists
        db.insertWithOnConflict(NAME, null, contentValuesOf(
            QUEST_TYPE to questType,
            SUCCEEDED to 0
        ), CONFLICT_IGNORE)

        // then increase by one
        db.execSQL("UPDATE $NAME SET $SUCCEEDED = $SUCCEEDED + 1 WHERE $QUEST_TYPE = ?", arrayOf(questType))
        listeners.forEach { it.onAddedOne(questType) }
    }

    fun subtractOne(questType: String) {
        db.execSQL("UPDATE $NAME SET $SUCCEEDED = $SUCCEEDED - 1 WHERE $QUEST_TYPE = ?", arrayOf(questType))
        listeners.forEach { it.onSubtractedOne(questType) }
    }

    fun getAmount(questType: String): Int {
        return db.queryOne(NAME, arrayOf(SUCCEEDED), "$QUEST_TYPE = ?", arrayOf(questType)) {
            it.getInt(0)
        } ?: 0
    }

    fun getAmount(questTypes: List<String>): Int {
        val questionMarks = Array(questTypes.size) { "?" }.joinToString(",")
        val query = "$QUEST_TYPE in ($questionMarks)"
        return db.queryOne(NAME, arrayOf("total($SUCCEEDED)"), query, questTypes.toTypedArray()) {
            it.getInt(0)
        } ?: 0
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
}
