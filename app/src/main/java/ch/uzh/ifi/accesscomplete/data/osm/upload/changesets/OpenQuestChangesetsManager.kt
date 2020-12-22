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

package ch.uzh.ifi.accesscomplete.data.osm.upload.changesets

import android.content.SharedPreferences
import android.util.Log
import ch.uzh.ifi.accesscomplete.data.MapDataApi

import de.westnordost.osmapi.common.errors.OsmConflictException
import ch.uzh.ifi.accesscomplete.ApplicationConstants.QUESTTYPE_TAG_KEY
import ch.uzh.ifi.accesscomplete.ApplicationConstants.USER_AGENT
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.ktx.toBcp47LanguageTag
import java.util.*

import javax.inject.Inject

/** Manages the creation and reusage of quest-related changesets */
class OpenQuestChangesetsManager @Inject constructor(
    private val mapDataApi: MapDataApi,
    private val openChangesetsDB: OpenChangesetsDao,
    private val changesetAutoCloser: ChangesetAutoCloser,
    private val prefs: SharedPreferences
) {
    fun getOrCreateChangeset(questType: OsmElementQuestType<*>, source: String): Long {
        val openChangeset = openChangesetsDB.get(questType.name, source)
        return if (openChangeset?.changesetId != null) {
            openChangeset.changesetId
        } else {
            createChangeset(questType, source)
        }
    }

    fun createChangeset(questType: OsmElementQuestType<*>, source: String): Long {
        val changesetId = mapDataApi.openChangeset(createChangesetTags(questType, source))
        openChangesetsDB.put(OpenChangeset(questType.name, source, changesetId))
        changesetAutoCloser.enqueue(CLOSE_CHANGESETS_AFTER_INACTIVITY_OF)
        Log.i(TAG, "Created changeset #$changesetId")
        return changesetId
    }

    @Synchronized fun closeOldChangesets() {
        val timePassed = System.currentTimeMillis() - prefs.getLong(Prefs.LAST_SOLVED_QUEST_TIME, 0)
        if (timePassed < CLOSE_CHANGESETS_AFTER_INACTIVITY_OF) return

        for (info in openChangesetsDB.getAll()) {
            try {
                //mapDataApi.closeChangeset(info.changesetId)
                Log.i(TAG, "Closed changeset #${info.changesetId}")
            } catch (e: OsmConflictException) {
                Log.w(TAG, "Couldn't close changeset #${info.changesetId} because it has already been closed")
            } finally {
                openChangesetsDB.delete(info.questType, info.source)
            }
        }
    }

    private fun createChangesetTags(questType: OsmElementQuestType<*>, source: String) =
        mapOf(
            "comment" to questType.commitMessage,
            "created_by" to USER_AGENT,
            "locale" to Locale.getDefault().toBcp47LanguageTag(),
            QUESTTYPE_TAG_KEY to questType.name,
            "source" to source
        )

    companion object {
        private const val TAG = "ChangesetManager"
    }
}

private const val CLOSE_CHANGESETS_AFTER_INACTIVITY_OF = 1000L * 60 * 20 // 20min

private val QuestType<*>.name get() = javaClass.simpleName
