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

import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuest
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestController
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestDao
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNoteDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuest
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/** Access and listen to how many unsynced (=uploadable) changes there are */
@Singleton class UnsyncedChangesCountSource @Inject constructor(
    private val osmQuestController: OsmQuestController,
    private val osmNoteQuestController: OsmNoteQuestController,
    private val createNoteDao: CreateNoteDao,
    private val splitWayDao: OsmQuestSplitWayDao,
    private val undoOsmQuestDao: UndoOsmQuestDao
) {
    private val listeners: MutableList<UnsyncedChangesCountListener> = CopyOnWriteArrayList()

    val count: Int get() =
            answeredOsmQuestCount +
            answeredOsmNoteQuestCount +
            splitWayCount +
            createNoteCount +
            undoOsmQuestCount

    val questCount: Int get() = answeredOsmQuestCount + splitWayCount - undoOsmQuestCount

    private var answeredOsmQuestCount: Int = osmQuestController.getAllAnsweredCount()
    set(value) {
        val diff = value - field
        field = value
        onUpdate(diff)
    }
    private var answeredOsmNoteQuestCount: Int = osmNoteQuestController.getAllAnsweredCount()
    set(value) {
        val diff = value - field
        field = value
        onUpdate(diff)
    }
    private var splitWayCount: Int = splitWayDao.getCount()
    set(value) {
        val diff = value - field
        field = value
        onUpdate(diff)
    }
    private var createNoteCount: Int = createNoteDao.getCount()
    set(value) {
        val diff = value - field
        field = value
        onUpdate(diff)
    }
    private var undoOsmQuestCount: Int = undoOsmQuestDao.getCount()
    set(value) {
        val diff = value - field
        field = value
        onUpdate(diff)
    }

    private val splitWayListener = object : OsmQuestSplitWayDao.Listener {
        override fun onAddedSplitWay() { ++splitWayCount }
        override fun onDeletedSplitWay() { --splitWayCount }
    }
    private val undoOsmQuestListener = object : UndoOsmQuestDao.Listener {
        override fun onAddedUndoOsmQuest() { ++undoOsmQuestCount }
        override fun onDeletedUndoOsmQuest() { --undoOsmQuestCount }
    }
    private val createNoteListener = object : CreateNoteDao.Listener {
        override fun onAddedCreateNote() { ++createNoteCount }
        override fun onDeletedCreateNote() { --createNoteCount }
    }
    private val noteQuestStatusListener = object : OsmNoteQuestController.QuestStatusListener {
        override fun onAdded(quest: OsmNoteQuest) {
            if (quest.status.isAnswered) { ++answeredOsmNoteQuestCount }
        }

        override fun onChanged(quest: OsmNoteQuest, previousStatus: QuestStatus) {
            if(quest.status.isAnswered && !previousStatus.isAnswered) {
                ++answeredOsmNoteQuestCount
            } else if (!quest.status.isAnswered && previousStatus.isAnswered) {
                --answeredOsmNoteQuestCount
            }
        }

        override fun onRemoved(questId: Long, previousStatus: QuestStatus) {
            if (previousStatus.isAnswered) { --answeredOsmNoteQuestCount }
        }

        override fun onUpdated(added: Collection<OsmNoteQuest>, updated: Collection<OsmNoteQuest>, deleted: Collection<Long>) {
            answeredOsmNoteQuestCount = osmNoteQuestController.getAllAnsweredCount()
        }
    }
    private val questStatusListener = object : OsmQuestController.QuestStatusListener {
        override fun onChanged(quest: OsmQuest, previousStatus: QuestStatus) {
            if(quest.status.isAnswered && !previousStatus.isAnswered) {
                ++answeredOsmQuestCount
            } else if (!quest.status.isAnswered && previousStatus.isAnswered) {
                --answeredOsmQuestCount
            }
        }

        override fun onRemoved(questId: Long, previousStatus: QuestStatus) {
            if (previousStatus.isAnswered) { --answeredOsmQuestCount }
        }

        override fun onUpdated(added: Collection<OsmQuest>, updated: Collection<OsmQuest>, deleted: Collection<Long>) {
            answeredOsmQuestCount = osmQuestController.getAllAnsweredCount()
        }
    }

    init {
        splitWayDao.addListener(splitWayListener)
        undoOsmQuestDao.addListener(undoOsmQuestListener)
        createNoteDao.addListener(createNoteListener)
        osmNoteQuestController.addQuestStatusListener(noteQuestStatusListener)
        osmQuestController.addQuestStatusListener(questStatusListener)
    }

    private fun onUpdate(diff: Int) {
        if (diff > 0) listeners.forEach { it.onUnsyncedChangesCountIncreased() }
        else if (diff < 0) listeners.forEach { it.onUnsyncedChangesCountDecreased() }
    }

    fun addListener(listener: UnsyncedChangesCountListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: UnsyncedChangesCountListener) {
        listeners.remove(listener)
    }
}

interface UnsyncedChangesCountListener {
    fun onUnsyncedChangesCountIncreased()
    fun onUnsyncedChangesCountDecreased()
}

private val QuestStatus.isAnswered get() = this == QuestStatus.ANSWERED
