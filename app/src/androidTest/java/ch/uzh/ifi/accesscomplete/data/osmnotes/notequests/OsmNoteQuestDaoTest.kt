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

package ch.uzh.ifi.accesscomplete.data.osmnotes.notequests

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteMapping
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.notes.Note
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class OsmNoteQuestDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: OsmNoteQuestDao
    private lateinit var noteDao: NoteDao
    private lateinit var questType: OsmNoteQuestType

    @Before fun createDao() {
        questType = OsmNoteQuestType()
        val noteMapping = NoteMapping(serializer)
        dao = OsmNoteQuestDao(dbHelper, OsmNoteQuestMapping(serializer, questType, noteMapping))
        noteDao = NoteDao(dbHelper, noteMapping)
    }

    @Test fun addGetNoChanges() {
        val quest = create()
        addToDaos(quest)
        checkEqual(quest, dao.get(quest.id!!))
    }

    @Test fun addGetWithChanges() {
        val quest = create(
            status = QuestStatus.CLOSED,
            comment = "hi da du",
            imagePaths = listOf("blubbi", "diblub")
        )
        addToDaos(quest)

        checkEqual(quest, dao.get(quest.id!!))
    }

    @Test fun deleteButNothingIsThere() {
        assertFalse(dao.delete(1L))
    }

    @Test fun addAndDelete() {
        val quest = create()
        addToDaos(quest)

        assertTrue(dao.delete(quest.id!!))
        assertNull(dao.get(quest.id!!))
        assertFalse(dao.delete(quest.id!!))
    }

    @Test fun update() {
        val quest = create()
        addToDaos(quest)

        quest.status = QuestStatus.HIDDEN
        quest.comment = "ho"
        quest.imagePaths = listOf("bla", "blu")

        dao.update(quest)

        checkEqual(quest, dao.get(quest.id!!))
    }

    @Test fun addAllAndDeleteAll() {
        val notes = listOf(createNote(1), createNote(2), createNote(3))
        noteDao.putAll(notes)

        val quests = notes.map { create(it) }
        assertEquals(3, dao.addAll(quests))

        for (quest in quests) {
            assertNotNull(quest.id)
            checkEqual(quest, dao.get(quest.id!!))
        }
        assertEquals(3, dao.deleteAllIds(quests.map { it.id!! }))
        assertEquals(0, dao.getCount())
    }

    @Test fun addSameNoteTwiceDoesntWork() {
        val note = createNote()
        noteDao.put(note)
        dao.add(create(note, status = QuestStatus.NEW))

        val questForSameNote = create(note, QuestStatus.HIDDEN)
        assertFalse(dao.add(questForSameNote))

        val quests = dao.getAll()
        assertEquals(QuestStatus.NEW, quests.single().status)
        assertNull(questForSameNote.id)
    }

    @Test fun replaceSameNoteDoesWork() {
        val note = createNote()
        noteDao.put(note)
        dao.add(create(note, QuestStatus.NEW))

        val questForSameNote = create(note, QuestStatus.HIDDEN)
        assertTrue(dao.replace(questForSameNote))

        val quests = dao.getAll()
        assertEquals(QuestStatus.HIDDEN, quests.single().status)
        assertNotNull(questForSameNote.id)
    }

    @Test fun getAllByBBox() {
        addToDaos(
            create(noteId = 1, notePosition = OsmLatLon(5.0, 5.0)),
            create(noteId = 2, notePosition = OsmLatLon(11.0, 11.0))
        )

        assertEquals(1, dao.getAll(bounds = BoundingBox(0.0, 0.0, 10.0, 10.0)).size)
        assertEquals(2, dao.getAll().size)
    }

    @Test fun getAllByStatus() {
        addToDaos(
            create(noteId = 1, status = QuestStatus.HIDDEN),
            create(noteId = 2, status = QuestStatus.NEW)
        )

        assertEquals(1, dao.getAll(statusIn = listOf(QuestStatus.HIDDEN)).size)
        assertEquals(1, dao.getAll(statusIn = listOf(QuestStatus.NEW)).size)
        assertEquals(0, dao.getAll(statusIn = listOf(QuestStatus.CLOSED)).size)
        assertEquals(2, dao.getAll(statusIn = listOf(QuestStatus.NEW, QuestStatus.HIDDEN)).size)
    }

    @Test fun getCount() {
        addToDaos(create(1), create(2))
        assertEquals(2, dao.getCount())
    }

    @Test fun deleteAll() {
        addToDaos(create(1), create(2))
        assertEquals(2, dao.deleteAll())
    }

    private fun checkEqual(quest: OsmNoteQuest, dbQuest: OsmNoteQuest?) {
        assertNotNull(dbQuest)
        assertEquals(quest.lastUpdate, dbQuest!!.lastUpdate)
        assertEquals(quest.status, dbQuest.status)
        assertEquals(quest.center, dbQuest.center)
        assertEquals(quest.comment, dbQuest.comment)
        assertEquals(quest.id, dbQuest.id)
        assertEquals(quest.type, dbQuest.type)
        // note saving already tested in NoteDaoTest
    }

    private fun createNote(id: Long = 5, position: LatLon = OsmLatLon(1.0, 1.0)) = Note().also {
        it.id = id
        it.position = position
        it.status = Note.Status.OPEN
        it.dateCreated = Date(5000)
    }

    private fun create(
            noteId: Long = 1,
            status: QuestStatus = QuestStatus.NEW,
            notePosition: LatLon = OsmLatLon(1.0, 1.0),
            comment: String? = null,
            imagePaths: List<String>? = null
    ) = create( createNote(noteId, notePosition), status, comment, imagePaths)

    private fun create(
            note: Note,
            status: QuestStatus = QuestStatus.NEW,
            comment: String? = null,
            imagePaths: List<String>? = null
    ) = OsmNoteQuest(null, note, status, comment, Date(5000), questType, imagePaths)

    private fun addToDaos(vararg quests: OsmNoteQuest) {
        for (quest in quests) {
            noteDao.put(quest.note)
            dao.add(quest)
        }
    }
}
