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

import ch.uzh.ifi.accesscomplete.data.NotesApi
import de.westnordost.osmapi.common.Handler
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.notes.Note
import de.westnordost.osmapi.notes.NoteComment
import ch.uzh.ifi.accesscomplete.ApplicationConstants.USER_AGENT
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.ElementKey
import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNoteWithPhotosUploader
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class SingleCreateNoteUploaderTest {
    private lateinit var osmNoteUploader: OsmNoteWithPhotosUploader
    private lateinit var notesApi: NotesApi
    private lateinit var uploader: SingleCreateNoteUploader

    @Before fun setUp() {
        notesApi = mock()
        osmNoteUploader = mock()
        uploader = SingleCreateNoteUploader(osmNoteUploader, notesApi)
    }

    @Test fun `upload createNote on existing note will comment on existing note`() {
        val createNote = CreateNote(1, "jo", OsmLatLon(1.0, 2.0), null, ElementKey(Element.Type.WAY, 5L))

        val existingNote = newNote(createNote)
        setUpExistingNote(existingNote)
        on(osmNoteUploader.comment(anyLong(), anyString(), isNull())).thenReturn(existingNote)

        assertEquals(existingNote, uploader.upload(createNote))

        verify(osmNoteUploader).comment(existingNote.id, createNote.text, null)
    }

    @Test(expected = ConflictException::class)
    fun `upload createNote on existing closed note will throw conflict exception`() {
        val createNote = CreateNote(1, "jo", OsmLatLon(1.0, 2.0), null, ElementKey(Element.Type.WAY, 5L))

        val existingNote = newNote(createNote)
        existingNote.status = Note.Status.CLOSED
        setUpExistingNote(existingNote)

        on(osmNoteUploader.comment(anyLong(), anyString(), isNull())).thenThrow(ConflictException())

        uploader.upload(createNote)

        verify(notesApi).getAll(any(),any(),anyInt(),anyInt())
        verifyNoMoreInteractions(notesApi)
    }

    @Test fun `upload createNote with no associated element works`() {
        val createNote = CreateNote(1, "jo", OsmLatLon(1.0, 2.0))
        val note = newNote(null)

        on(osmNoteUploader.create(any(), anyString(), isNull())).thenReturn(note)

        assertEquals(note, uploader.upload(createNote))

        verify(osmNoteUploader).create(
            createNote.position,
            createNote.text + "\n\nvia " + USER_AGENT,
            null
        )
    }

    @Test fun `upload createNote with no quest title but associated element works`() {
        val createNote = CreateNote(1, "jo", OsmLatLon(1.0, 2.0), null, ElementKey(Element.Type.WAY, 5L))
        val note = newNote(null)

        on(osmNoteUploader.create(any(), anyString(), isNull())).thenReturn(note)

        assertEquals(note, uploader.upload(createNote))

        verify(osmNoteUploader).create(
            createNote.position,
            "for https://osm.org/way/5 via " + USER_AGENT + ":\n\n" + createNote.text,
            null
        )
    }

    @Test fun `upload createNote with associated element and no note yet works`() {
        val createNote = CreateNote(1, "jo", OsmLatLon(1.0, 2.0), "What?", ElementKey(Element.Type.WAY, 5L))

        val note = newNote(createNote)

        on(osmNoteUploader.create(any(), anyString(), isNull())).thenReturn(note)

        assertEquals(note, uploader.upload(createNote))

        verify(osmNoteUploader).create(
            createNote.position,
            "Unable to answer \"What?\" for https://osm.org/way/5 via " + USER_AGENT + ":\n\n" + createNote.text,
            null
        )
    }

    private fun setUpExistingNote(note: Note) {
        doAnswer { invocation ->
            (invocation.arguments[1] as Handler<Note>).handle(note)
        }.on(notesApi).getAll(any(),any(),anyInt(),anyInt())
    }

    private fun newNote(fitsTo: CreateNote?): Note {
        val note = Note()
        note.id = 2
        note.status = Note.Status.OPEN
        note.dateCreated = Date()
        note.position = OsmLatLon(1.0, 2.0)
        val comment = NoteComment()
        comment.text = "bla bla"
        if (fitsTo != null) {
            comment.text += fitsTo.associatedElementString
        }
        comment.action = NoteComment.Action.OPENED
        comment.date = Date()
        note.comments.add(0, comment)
        return note
    }
}

private val CreateNote.associatedElementString: String? get() {
    val elementKey = elementKey ?: return null
    val lowercaseTypeName = elementKey.elementType.name.toLowerCase(Locale.UK)
    val elementId = elementKey.elementId
    return "https://osm.org/$lowercaseTypeName/$elementId"
}
