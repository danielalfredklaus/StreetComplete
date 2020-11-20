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

import android.util.Log
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Way
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementUpdateController
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenQuestChangesetsManager
import ch.uzh.ifi.accesscomplete.data.osm.upload.OsmInChangesetsUploader
import ch.uzh.ifi.accesscomplete.data.user.StatisticsUpdater
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/** Gets all split ways from local DB and uploads them via the OSM API */
class SplitWaysUploader @Inject constructor(
    changesetManager: OpenQuestChangesetsManager,
    private val elementUpdateController: OsmElementUpdateController,
    private val splitWayDB: OsmQuestSplitWayDao,
    private val splitSingleOsmWayUploader: SplitSingleWayUploader,
    private val statisticsUpdater: StatisticsUpdater
) : OsmInChangesetsUploader<OsmQuestSplitWay>(changesetManager, elementUpdateController) {

    @Synchronized override fun upload(cancelled: AtomicBoolean) {
        Log.i(TAG, "Splitting ways")
        super.upload(cancelled)
    }

    override fun getAll(): Collection<OsmQuestSplitWay> = splitWayDB.getAll()

    override fun uploadSingle(changesetId: Long, quest: OsmQuestSplitWay, element: Element): List<Element> {
        return splitSingleOsmWayUploader.upload(changesetId, element as Way, quest.splits)
    }

    override fun updateElement(element: Element, quest: OsmQuestSplitWay) {
        /* We override this because in case of a split, the two (or more) sections of the way should
        *  actually get the same quests as the original way, there is no need to again check for
        *  the eligibility of the element for each quest which would be done normally */
        elementUpdateController.update(element, quest.questTypesOnWay)
    }

    override fun onUploadSuccessful(quest: OsmQuestSplitWay) {
        splitWayDB.delete(quest.questId)
        statisticsUpdater.addOne(quest.osmElementQuestType.javaClass.simpleName, quest.position)
        Log.d(TAG, "Uploaded split way #${quest.wayId}")
    }

    override fun onUploadFailed(quest: OsmQuestSplitWay, e: Throwable) {
        splitWayDB.delete(quest.questId)
        Log.d(TAG, "Dropped split for way #${quest.wayId}: ${e.message}")
    }

    companion object {
        private const val TAG = "SplitOsmWayUpload"
    }
}
