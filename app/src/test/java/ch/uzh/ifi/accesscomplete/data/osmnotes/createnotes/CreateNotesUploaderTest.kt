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

package ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes

import ch.uzh.ifi.accesscomplete.data.MapDataApi
import org.junit.Before
import org.junit.Test

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.notes.Note
import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import ch.uzh.ifi.accesscomplete.on
import ch.uzh.ifi.accesscomplete.any

import org.mockito.Mockito.*
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.notes.NoteComment
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.ElementKey
import ch.uzh.ifi.accesscomplete.data.osmnotes.ImageUploadException
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.mock
import java.util.*

import java.util.concurrent.atomic.AtomicBoolean


class CreateNotesUploaderTest {
    private lateinit var createNoteDB: CreateNoteDao
    private lateinit var osmNoteQuestController: OsmNoteQuestController
    private lateinit var mapDataApi: MapDataApi
    private lateinit var questType: OsmNoteQuestType
    private lateinit var singleCreateNoteUploader: SingleCreateNoteUploader

    private lateinit var uploader: CreateNotesUploader

    @Before fun setUp() {
        mapDataApi = mock()
        osmNoteQuestController = mock()
        createNoteDB = mock()
        questType = mock()
        singleCreateNoteUploader = mock()

        uploader = CreateNotesUploader(createNoteDB, osmNoteQuestController, mapDataApi, questType,
                singleCreateNoteUploader)
    }

    @Test fun `cancel upload works`() {
        val cancelled = AtomicBoolean(true)
        uploader.upload(cancelled)
        verifyZeroInteractions(createNoteDB, osmNoteQuestController, mapDataApi, questType,
            singleCreateNoteUploader)
    }

    @Test fun `catches conflict exception`() {
        on(createNoteDB.getAll()).thenReturn(listOf(newCreateNote()))
        on(singleCreateNoteUploader.upload(any())).thenThrow(ConflictException())

        uploader.upload(AtomicBoolean(false))

        // will not throw ElementConflictException
    }

    @Test fun `delete each uploaded quest in local DB and call listener`() {
        val createNotes = listOf(newCreateNote(), newCreateNote())

        on(createNoteDB.getAll()).thenReturn(createNotes)
        on(singleCreateNoteUploader.upload(any())).thenReturn(newNote())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(createNoteDB, times(createNotes.size)).delete(anyLong())
        verify(osmNoteQuestController, times(createNotes.size)).add(any())
        verify(uploader.uploadedChangeListener, times(createNotes.size))?.onUploaded(any(), any())
    }

    @Test fun `delete each unsuccessfully uploaded quest in local DB and call listener`() {
        val createNotes = listOf(newCreateNote(), newCreateNote())

        on(createNoteDB.getAll()).thenReturn(createNotes)
        on(singleCreateNoteUploader.upload(any())).thenThrow(ConflictException())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(createNoteDB, times(createNotes.size)).delete(anyLong())
        verify(uploader.uploadedChangeListener, times(2))?.onDiscarded(any(), any())
    }

    @Test fun `discard if element was deleted`() {
        val createNote = CreateNote(1, "jo ho", OsmLatLon(1.0, 2.0), null, ElementKey(Element.Type.NODE, 1))

        on(createNoteDB.getAll()).thenReturn(listOf(createNote))
        on(mapDataApi.getNode(anyLong())).thenReturn(null)

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(uploader.uploadedChangeListener)?.onDiscarded("NOTE", createNote.position)
    }

    @Test fun `catches image upload exception`() {
        val note = CreateNote(1, "jo ho", OsmLatLon(1.0, 2.0), null, null, listOf("hello"))
        on(createNoteDB.getAll()).thenReturn(listOf(note))
        on(singleCreateNoteUploader.upload(any())).thenThrow(ImageUploadException())

        uploader.upload(AtomicBoolean(false))

        verify(createNoteDB, never()).delete(anyLong())
        // will not throw ElementConflictException and not delete the note from db
    }
}

private fun newNote(): Note {
    val note = Note()
    note.id = 2
    note.status = Note.Status.OPEN
    note.dateCreated = Date()
    note.position = OsmLatLon(1.0, 2.0)
    val comment = NoteComment()
    comment.text = "bla bla"
    comment.action = NoteComment.Action.OPENED
    comment.date = Date()
    note.comments.add(0, comment)
    return note
}

private fun newCreateNote() = CreateNote(1, "jo ho", OsmLatLon(1.0, 2.0), null, null, null)
