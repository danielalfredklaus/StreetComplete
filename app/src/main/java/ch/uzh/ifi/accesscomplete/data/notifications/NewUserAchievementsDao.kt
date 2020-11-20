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

package ch.uzh.ifi.accesscomplete.data.notifications

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.notifications.NewUserAchievementsTable.Columns.ACHIEVEMENT
import ch.uzh.ifi.accesscomplete.data.notifications.NewUserAchievementsTable.Columns.LEVEL
import ch.uzh.ifi.accesscomplete.data.notifications.NewUserAchievementsTable.NAME
import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.ktx.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Singleton

/** Stores which achievements have *newly* been unlocked by the user and which levels. */
@Singleton class NewUserAchievementsDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {
    /* Must be a singleton because there is a listener that should respond to a change in the
     *  database table */

    private val db get() = dbHelper.writableDatabase

    interface UpdateListener {
        fun onNewUserAchievementsUpdated()
    }

    private val listeners: MutableList<UpdateListener> = CopyOnWriteArrayList()

    fun pop(): Pair<String, Int>? {
        var result: Pair<String, Int>? = null
        db.transaction {
            val r = db.queryOne(NAME, orderBy = "$ACHIEVEMENT, $LEVEL ASC") {
                it.getString(ACHIEVEMENT) to it.getInt(LEVEL)
            }
            if (r != null) {
                val query = "$ACHIEVEMENT = ? AND $LEVEL = ?"
                val args = arrayOf(r.first, r.second.toString())
                db.delete(NAME, query, args)
                onNewUserAchievementsChanged()
            }
            result = r
        }
        return result
    }

    fun getCount(): Int {
        return db.queryOne(NAME, arrayOf("COUNT(*)")) { it.getInt(0) } ?: 0
    }

    fun push(achievementAndLevel: Pair<String, Int>) {
        val result = db.insertWithOnConflict(NAME, null, contentValuesOf(
            ACHIEVEMENT to achievementAndLevel.first,
            LEVEL to achievementAndLevel.second
        ), SQLiteDatabase.CONFLICT_IGNORE)
        if (result != -1L) {
            onNewUserAchievementsChanged()
        }
    }

    fun addListener(listener: UpdateListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: UpdateListener) {
        listeners.remove(listener)
    }

    private fun onNewUserAchievementsChanged() {
        for (listener in listeners) {
            listener.onNewUserAchievementsUpdated()
        }
    }
}
