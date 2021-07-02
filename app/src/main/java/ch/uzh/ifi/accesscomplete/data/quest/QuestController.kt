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

import android.content.SharedPreferences
import android.util.Log
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmElement
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.ElementKey
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.MergedElementDao
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuest
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestController
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuest
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestDao
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWay
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayDao
import ch.uzh.ifi.accesscomplete.data.osm.splitway.SplitPolylineAtPosition
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNote
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNoteDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.quests.note_discussion.NoteAnswer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/** Controls the workflow of quests: Solving them, hiding them instead, splitting the way instead,
 *  undoing, etc. */
@Singleton class QuestController @Inject constructor(
        private val osmQuestController: OsmQuestController,
        private val osmNoteQuestController: OsmNoteQuestController,
        private val undoOsmQuestDB: UndoOsmQuestDao,
        private val osmElementDB: MergedElementDao,
        private val splitWayDB: OsmQuestSplitWayDao,
        private val createNoteDB: CreateNoteDao,
        private val prefs: SharedPreferences
): CoroutineScope by CoroutineScope(Dispatchers.Default) {
    /** Create a note for the given OSM Quest instead of answering it. The quest will turn
     * invisible.
     * @return true if successful
     */
    fun createNote(osmQuestId: Long, questTitle: String, text: String, imagePaths: List<String>?): Boolean {
        val q = osmQuestController.get(osmQuestId)
        if (q?.status != QuestStatus.NEW) return false

        val createNote = CreateNote(null, text, q.center, questTitle, ElementKey(q.elementType, q.elementId), imagePaths)
        createNoteDB.add(createNote)

        /* The quests that reference the same element for which the user was not able to
           answer the question are removed because the to-be-created note blocks quest
           creation for other users, so those quests should be removed from the user's
           own display as well. As soon as the note is resolved, the quests will be re-
           created next time they are downloaded */
        removeUnsolvedQuestsForElement(q.elementType, q.elementId)
        return true
    }

    fun createNote(text: String, imagePaths: List<String>?, position: LatLon) {
        val createNote = CreateNote(null, text, position, null, null, imagePaths)
        createNoteDB.add(createNote)
    }

    private fun removeUnsolvedQuestsForElement(elementType: Element.Type, elementId: Long) {
        osmQuestController.deleteAllUnsolvedForElement(elementType, elementId)
        osmElementDB.deleteUnreferenced()
    }

    /** Split a way for the given OSM Quest. The quest will turn invisible.
     * @return true if successful
     */
    fun splitWay(osmQuestId: Long, splits: List<SplitPolylineAtPosition>, source: String): Boolean {
        val q = osmQuestController.get(osmQuestId)
        if (q?.status != QuestStatus.NEW) return false

        val unsolvedQuestTypes = osmQuestController.getAllUnsolvedQuestTypesForElement(q.elementType, q.elementId)
        splitWayDB.add(OsmQuestSplitWay(osmQuestId, q.osmElementQuestType, q.elementId, source, splits, unsolvedQuestTypes))

        removeUnsolvedQuestsForElement(q.elementType, q.elementId)
        return true
    }

    /** Apply the user's answer to the given quest. (The quest will turn invisible.)
     * @return true if successful
     */
    fun solve(questId: Long, group: QuestGroup, answer: Any, source: String): Boolean {
        return when(group) {
            QuestGroup.OSM -> solveOsmQuest(questId, answer, source)
            QuestGroup.OSM_NOTE -> solveOsmNoteQuest(questId, answer as NoteAnswer)
            QuestGroup.UZH -> solveUZHQuest(questId, answer, source)
        } //TODO: Add extra solve function for UZH
    }

    fun getOsmElement(quest: OsmQuest): OsmElement? =
        osmElementDB.get(quest.elementType, quest.elementId) as OsmElement?

    /** Undo changes made after answering a quest. */
    fun undo(quest: OsmQuest) {
        when(quest.status) {
            // not uploaded yet -> simply revert to NEW
            QuestStatus.ANSWERED, QuestStatus.HIDDEN -> {
                osmQuestController.undo(quest)
            }
            // already uploaded! -> create change to reverse the previous change
            QuestStatus.CLOSED -> {
                osmQuestController.revert(quest)
                undoOsmQuestDB.add(UndoOsmQuest(quest))
            }
            else -> {
                throw IllegalStateException("Tried to undo a quest that hasn't been answered yet")
            }
        }
    }

    private fun solveOsmNoteQuest(questId: Long, answer: NoteAnswer): Boolean {
        val q = osmNoteQuestController.get(questId)
        if (q == null || q.status !== QuestStatus.NEW) return false

        require(answer.text.isNotEmpty()) { "NoteQuest has been answered with an empty comment!" }

        osmNoteQuestController.answer(q, answer)
        return true
    }

    private fun solveOsmQuest(questId: Long, answer: Any, source: String): Boolean {
        // race condition: another thread (i.e. quest download thread) may have removed the
        // element already (#282). So in this case, just ignore
        val q = osmQuestController.get(questId)
        if (q?.status != QuestStatus.NEW) return false
        val element = osmElementDB.get(q.elementType, q.elementId) ?: return false

        val changes = createOsmQuestChanges(q, element, answer)
        if (changes == null) {
            // if applying the changes results in an error (=a conflict), the data the quest(ion)
            // was based on is not valid anymore -> like with other conflicts, silently drop the
            // user's change (#289) and the quest
            osmQuestController.fail(q)
            return false
        } else {
            require(!changes.isEmpty()) {
                "OsmQuest $questId (${q.type.javaClass.simpleName}) has been answered by the user but the changeset is empty!"
            }

            Log.d(TAG, "Solved a ${q.type.javaClass.simpleName} quest: $changes")
            osmQuestController.answer(q, changes, source)
            prefs.edit().putLong(Prefs.LAST_SOLVED_QUEST_TIME, System.currentTimeMillis()).apply()
            return true
        }
    }

    private fun solveUZHQuest(questId: Long, answer: Any, source: String): Boolean {
        //TODO: Finish this implementation
        return false

    }

    private fun createOsmQuestChanges(quest: OsmQuest, element: Element, answer: Any) : StringMapChanges? {
        return try {
            val changesBuilder = StringMapChangesBuilder(element.tags.orEmpty())
            quest.osmElementQuestType.applyAnswerToUnsafe(answer, changesBuilder)
            changesBuilder.create()
        } catch (e: IllegalArgumentException) {
            // applying the changes results in an error (=a conflict)
            null
        }
    }

    /** Make the given quest invisible (per user interaction).  */
    fun hide(questId: Long, group: QuestGroup) {
        when (group) {
            QuestGroup.OSM -> {
                val quest = osmQuestController.get(questId)
                if (quest?.status != QuestStatus.NEW) return
                osmQuestController.hide(quest)
            }
            QuestGroup.OSM_NOTE -> {
                val q = osmNoteQuestController.get(questId)
                if (q?.status != QuestStatus.NEW) return
                osmNoteQuestController.hide(q)
            }
            QuestGroup.UZH -> {
                //TODO: Add implementation for UZH
            }
        }
    }

    /** Retrieve the given quest from local database  */
    fun get(questId: Long, group: QuestGroup): Quest? = when (group) {
        QuestGroup.OSM -> osmQuestController.get(questId)
        QuestGroup.OSM_NOTE -> osmNoteQuestController.get(questId)
        QuestGroup.UZH -> null //TODO: Add get for QuestGroup.UZH
    }

    companion object {
        private const val TAG = "QuestController"
    }
}

data class QuestAndGroup(val quest: Quest, val group: QuestGroup)
