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

import de.westnordost.osmapi.notes.Note
import org.junit.Before

import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import ch.uzh.ifi.accesscomplete.on
import ch.uzh.ifi.accesscomplete.any
import org.junit.Test

import org.mockito.Mockito.*
import de.westnordost.osmapi.map.data.OsmLatLon
import ch.uzh.ifi.accesscomplete.data.osmnotes.ImageUploadException
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNoteWithPhotosUploader
import ch.uzh.ifi.accesscomplete.mock
import java.util.concurrent.atomic.AtomicBoolean


class OsmNoteQuestsChangesUploaderTest {
    private lateinit var osmNoteQuestController: OsmNoteQuestController
    private lateinit var singleNoteUploader: OsmNoteWithPhotosUploader
    private lateinit var uploader: OsmNoteQuestsChangesUploader

    @Before fun setUp() {
        osmNoteQuestController = mock()
        singleNoteUploader = mock()
        uploader = OsmNoteQuestsChangesUploader(osmNoteQuestController, singleNoteUploader)
    }

    @Test fun `cancel upload works`() {
        uploader.upload(AtomicBoolean(true))
        verifyZeroInteractions(singleNoteUploader, osmNoteQuestController)
    }

    @Test fun `catches conflict exception`() {
        on(osmNoteQuestController.getAllAnswered()).thenReturn(listOf(createQuest()))
        on(singleNoteUploader.comment(anyLong(),any(),any())).thenThrow(ConflictException())

        uploader.upload(AtomicBoolean(false))

        // will not throw ElementConflictException
    }

    @Test fun `close each uploaded quest in local DB and call listener`() {
        val quests = listOf(createQuest(), createQuest())

        on(osmNoteQuestController.getAllAnswered()).thenReturn(quests)
        on(singleNoteUploader.comment(anyLong(),any(),any())).thenReturn(createNote())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        for (quest in quests) {
            verify(osmNoteQuestController).success(quest)
        }
        verify(uploader.uploadedChangeListener, times(quests.size))?.onUploaded(any(), any())
    }

    @Test fun `delete each unsuccessfully uploaded quest in local DB and call listener`() {
        val quests = listOf(createQuest(), createQuest())

        on(osmNoteQuestController.getAllAnswered()).thenReturn(quests)
        on(singleNoteUploader.comment(anyLong(),any(),any())).thenThrow(ConflictException())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(osmNoteQuestController, times(quests.size)).fail(any())
        verify(uploader.uploadedChangeListener, times(2))?.onDiscarded(any(), any())
    }

    @Test fun `catches image upload exception`() {
        val quest = createQuest()
        quest.imagePaths = listOf("hello")
        on(osmNoteQuestController.getAllAnswered()).thenReturn(listOf(quest))
        on(singleNoteUploader.comment(anyLong(),any(),any())).thenThrow(ImageUploadException())

        uploader.upload(AtomicBoolean(false))

        // will not throw ElementConflictException, nor delete the note from db, nor update it
        verify(osmNoteQuestController, never()).fail(any())
        verify(osmNoteQuestController, never()).success(any())
    }
}

private fun createNote(): Note {
    val note = Note()
    note.id = 1
    note.position = OsmLatLon(1.0, 2.0)
    return note
}

private fun createQuest(): OsmNoteQuest {
    val quest = OsmNoteQuest(createNote(), OsmNoteQuestType())
    quest.id = 3
    quest.status = QuestStatus.NEW
    quest.comment = "blablub"
    return quest
}
