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

package ch.uzh.ifi.accesscomplete.data.visiblequests

import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.visiblequests.QuestVisibilityTable.Columns.QUEST_TYPE
import ch.uzh.ifi.accesscomplete.data.visiblequests.QuestVisibilityTable.Columns.VISIBILITY
import ch.uzh.ifi.accesscomplete.data.visiblequests.QuestVisibilityTable.NAME
import ch.uzh.ifi.accesscomplete.ktx.getInt
import ch.uzh.ifi.accesscomplete.ktx.getString
import ch.uzh.ifi.accesscomplete.ktx.query
import javax.inject.Singleton

/** Stores which quest types are visible by user selection and which are not */
@Singleton class VisibleQuestTypeDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {

    /* Is a singleton because it has a in-memory cache that is synchronized with changes made on
       the DB */

    private val cache: MutableMap<String, Boolean> by lazy { loadQuestTypeVisibilities() }

    private val db get() = dbHelper.writableDatabase

    private fun loadQuestTypeVisibilities(): MutableMap<String, Boolean> {
        val result = mutableMapOf<String,Boolean>()
        db.query(NAME) { cursor ->
            val questTypeName = cursor.getString(QUEST_TYPE)
            val visible = cursor.getInt(VISIBILITY) != 0
            result[questTypeName] = visible
        }
        return result
    }

    @Synchronized fun isVisible(questType: QuestType<*>): Boolean {
        val questTypeName = questType.javaClass.simpleName
        return cache[questTypeName] ?: (questType.defaultDisabledMessage <= 0)
    }

    @Synchronized fun setVisible(questType: QuestType<*>, visible: Boolean) {
        val questTypeName = questType.javaClass.simpleName
        db.replaceOrThrow(NAME, null, contentValuesOf(
            QUEST_TYPE to questTypeName,
            VISIBILITY to if (visible) 1 else 0
        ))
        cache[questTypeName] = visible
    }

    @Synchronized fun clear() {
        db.delete(NAME, null, null)
        cache.clear()
    }
}
