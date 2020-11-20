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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import android.util.Log
import de.westnordost.osmapi.map.data.Element
import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenQuestChangesetsManager
import ch.uzh.ifi.accesscomplete.data.osm.upload.OsmInChangesetsUploader
import ch.uzh.ifi.accesscomplete.data.user.StatisticsUpdater
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/** Gets all answered osm quests from local DB and uploads them via the OSM API */
class OsmQuestsUploader @Inject constructor(
    changesetManager: OpenQuestChangesetsManager,
    elementUpdateController: OsmElementUpdateController,
    private val osmQuestController: OsmQuestController,
    private val singleChangeUploader: SingleOsmElementTagChangesUploader,
    private val statisticsUpdater: StatisticsUpdater
) : OsmInChangesetsUploader<OsmQuest>(changesetManager, elementUpdateController) {

    @Synchronized override fun upload(cancelled: AtomicBoolean) {
        Log.i(TAG, "Applying quest changes")
        super.upload(cancelled)
    }

    override fun getAll(): Collection<OsmQuest> = osmQuestController.getAllAnswered()

    override fun uploadSingle(changesetId: Long, quest: OsmQuest, element: Element): List<Element> {
        return listOf(singleChangeUploader.upload(changesetId, quest, element))
    }

    override fun onUploadSuccessful(quest: OsmQuest) {
        osmQuestController.success(quest)
        statisticsUpdater.addOne(quest.osmElementQuestType.javaClass.simpleName, quest.center)
        Log.d(TAG, "Uploaded osm quest ${quest.toLogString()}")
    }

    override fun onUploadFailed(quest: OsmQuest, e: Throwable) {
        osmQuestController.fail(quest)
        Log.d(TAG, "Dropped osm quest ${quest.toLogString()}: ${e.message}")
    }

    override fun cleanUp(questTypes: Set<OsmElementQuestType<*>>) {
        super.cleanUp(questTypes)
        osmQuestController.cleanUp()
    }

    companion object {
        private const val TAG = "OsmQuestUpload"
    }
}

private fun OsmQuest.toLogString() =
    type.javaClass.simpleName + " for " + elementType.name.toLowerCase(Locale.US) + " #" + elementId
