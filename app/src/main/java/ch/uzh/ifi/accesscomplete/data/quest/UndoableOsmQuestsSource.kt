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
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/** Access and listen to undoable (closed, hidden, answered) changes there are.
 *
 *  Currently, only OsmQuests are undoable. If more things become undoable or conditionally
 *  undoable, the structure of this needs to be re-thought. Probably undoable things should
 *  implement an interface, and the source for this would return those instead of quests. */
@Singleton class UndoableOsmQuestsSource @Inject constructor(
    private val osmQuestController: OsmQuestController
){
    private val listeners: MutableList<UndoableOsmQuestsCountListener> = CopyOnWriteArrayList()

    var count: Int = osmQuestController.getAllUndoableCount()
        set(value) {
            val diff = value - field
            field = value
            onUpdate(diff)
        }

    private val questStatusListener = object : OsmQuestController.QuestStatusListener {
        override fun onChanged(quest: OsmQuest, previousStatus: QuestStatus) {
            if(quest.status.isUndoable && !previousStatus.isUndoable) {
                ++count
            } else if (!quest.status.isUndoable && previousStatus.isUndoable) {
                --count
            }
        }

        override fun onRemoved(questId: Long, previousStatus: QuestStatus) {
            if (previousStatus.isUndoable) {
                --count
            }
        }

        override fun onUpdated(added: Collection<OsmQuest>, updated: Collection<OsmQuest>, deleted: Collection<Long>) {
            count = osmQuestController.getAllUndoableCount()
        }
    }

    init {
        osmQuestController.addQuestStatusListener(questStatusListener)
    }

    /** Get the last undoable quest (includes answered, hidden and uploaded) */
    fun getLastUndoable(): OsmQuest? = osmQuestController.getLastUndoable()

    fun addListener(listener: UndoableOsmQuestsCountListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: UndoableOsmQuestsCountListener) {
        listeners.remove(listener)
    }

    private fun onUpdate(diff: Int) {
        if (diff > 0) listeners.forEach { it.onUndoableOsmQuestsCountIncreased() }
        else if (diff < 0) listeners.forEach { it.onUndoableOsmQuestsCountDecreased() }
    }
}

interface UndoableOsmQuestsCountListener {
    fun onUndoableOsmQuestsCountIncreased()
    fun onUndoableOsmQuestsCountDecreased()
}

private val QuestStatus.isUndoable: Boolean get() =
    when(this) {
        QuestStatus.ANSWERED, QuestStatus.HIDDEN, QuestStatus.CLOSED -> true
        else -> false
    }
