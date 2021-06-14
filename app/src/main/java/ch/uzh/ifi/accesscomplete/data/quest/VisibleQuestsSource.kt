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

package ch.uzh.ifi.accesscomplete.data.quest

import de.westnordost.osmapi.map.data.BoundingBox
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuest
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuest
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.visiblequests.VisibleQuestTypeDao
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/** Access and listen to quests visible on the map */
@Singleton class VisibleQuestsSource @Inject constructor(
    private val osmQuestController: OsmQuestController,
    private val osmNoteQuestController: OsmNoteQuestController,
    private val visibleQuestTypeDao: VisibleQuestTypeDao
) {
    private val listeners: MutableList<VisibleQuestListener> = CopyOnWriteArrayList()

    private val osmQuestStatusListener = object : OsmQuestController.QuestStatusListener {
        override fun onChanged(quest: OsmQuest, previousStatus: QuestStatus) {
            if (quest.status.isVisible && !previousStatus.isVisible) {
                onQuestBecomesVisible(quest, QuestGroup.OSM)
            } else if(!quest.status.isVisible && previousStatus.isVisible) {
                onQuestBecomesInvisible(quest.id!!, QuestGroup.OSM)
            }
        }

        override fun onRemoved(questId: Long, previousStatus: QuestStatus) {
            if (previousStatus.isVisible) {
                onQuestBecomesInvisible(questId, QuestGroup.OSM)
            }
        }

        override fun onUpdated(added: Collection<OsmQuest>, updated: Collection<OsmQuest>, deleted: Collection<Long>) {
            onUpdatedVisibleQuests(
                added.filter { visibleQuestTypeDao.isVisible(it.type) },
                updated.filter { visibleQuestTypeDao.isVisible(it.type) },
                deleted,
                QuestGroup.OSM
            )
        }
    }

    private val osmNoteQuestStatusListener = object : OsmNoteQuestController.QuestStatusListener {
        override fun onAdded(quest: OsmNoteQuest) {
            if(quest.status.isVisible) {
                onQuestBecomesVisible(quest, QuestGroup.OSM_NOTE)
            }
        }

        override fun onChanged(quest: OsmNoteQuest, previousStatus: QuestStatus) {
            if (quest.status.isVisible && !previousStatus.isVisible) {
                onQuestBecomesVisible(quest, QuestGroup.OSM_NOTE)
            } else if(!quest.status.isVisible && previousStatus.isVisible) {
                onQuestBecomesInvisible(quest.id!!, QuestGroup.OSM_NOTE)
            }
        }

        override fun onRemoved(questId: Long, previousStatus: QuestStatus) {
            if (previousStatus.isVisible) {
                onQuestBecomesInvisible(questId, QuestGroup.OSM_NOTE)
            }
        }

        override fun onUpdated(added: Collection<OsmNoteQuest>, updated: Collection<OsmNoteQuest>, deleted: Collection<Long>) {
            onUpdatedVisibleQuests(added, updated, deleted, QuestGroup.OSM_NOTE)
        }
    }

    init {
        osmQuestController.addQuestStatusListener(osmQuestStatusListener)
        osmNoteQuestController.addQuestStatusListener(osmNoteQuestStatusListener)
    }


    /** Get count of all unanswered quests in given bounding box */
    fun getAllVisibleCount(bbox: BoundingBox): Int {
        return osmQuestController.getAllVisibleInBBoxCount(bbox) +
                osmNoteQuestController.getAllVisibleInBBoxCount(bbox)
    }

    /** Retrieve all visible (=new) quests in the given bounding box from local database */
    fun getAllVisible(bbox: BoundingBox, questTypes: Collection<String>): List<QuestAndGroup> {
        if (questTypes.isEmpty()) return listOf() //TODO: Might require the addition of an UZH QuestController
        val osmQuests = osmQuestController.getAllVisibleInBBox(bbox, questTypes)
        val osmNoteQuests = osmNoteQuestController.getAllVisibleInBBox(bbox)
        //TODO: Probably add val UZHQuests = uzhQuestController.getAllVisibleInBBox(bbox)
        //TODO: https://github.com/westnordost/osmapi/blob/master/libs/core/src/main/java/de/westnordost/osmapi/map/data/BoundingBox.java
        return osmQuests.map { QuestAndGroup(it, QuestGroup.OSM) } +
                osmNoteQuests.map { QuestAndGroup(it, QuestGroup.OSM_NOTE) }
    } //TODO: Do I need an uzhQuestStatusListener too? The other two types have one each

    fun addListener(listener: VisibleQuestListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: VisibleQuestListener) {
        listeners.remove(listener)
    }

    private fun onQuestBecomesVisible(quest: Quest, group: QuestGroup) {
        listeners.forEach { it.onUpdatedVisibleQuests(listOf(quest), emptyList(), group) }
    }
    private fun onQuestBecomesInvisible(questId: Long, group: QuestGroup) {
        listeners.forEach { it.onUpdatedVisibleQuests(emptyList(), listOf(questId), group) }
    }
    private fun onUpdatedVisibleQuests(added: Collection<Quest>, updated: Collection<Quest>, deleted: Collection<Long>, group: QuestGroup) {
        val addedQuests = added.filter { it.status.isVisible } + updated.filter { it.status.isVisible }
        val deletedQuestIds = updated.filter { !it.status.isVisible }.map { it.id!! } + deleted
        listeners.forEach { it.onUpdatedVisibleQuests(addedQuests, deletedQuestIds, group) }
    }

}

interface VisibleQuestListener {
    fun onUpdatedVisibleQuests(added: Collection<Quest>, removed: Collection<Long>, group: QuestGroup)
}
