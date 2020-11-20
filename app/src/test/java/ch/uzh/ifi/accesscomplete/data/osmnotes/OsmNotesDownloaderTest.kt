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

package ch.uzh.ifi.accesscomplete.data.osmnotes

import android.content.SharedPreferences
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.data.NotesApi
import de.westnordost.osmapi.common.Handler
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.notes.Note
import de.westnordost.osmapi.notes.NoteComment
import de.westnordost.osmapi.user.User
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.util.*

class OsmNotesDownloaderTest {
    private lateinit var notesApi: NotesApi
    private lateinit var osmNoteQuestController: OsmNoteQuestController
    private lateinit var preferences: SharedPreferences
    private lateinit var avatarsDownloader: OsmAvatarsDownloader

    @Before fun setUp() {
        notesApi = mock()
        osmNoteQuestController = mock()
        on(osmNoteQuestController.replaceInBBox(any(), any())).thenReturn(OsmNoteQuestController.UpdateResult(0,0,0))

        preferences = mock()
        avatarsDownloader = mock()
    }

    @Test fun `downloads avatars of all users involved in note discussions`() {
        val note1 = createANote(4L)
        note1.comments.addAll(listOf(
            NoteComment().apply {
                date = Date()
                action = NoteComment.Action.COMMENTED
                text = "abc"
                user = User(54, "Blibu")
            },
            NoteComment().apply {
                date = Date()
                action = NoteComment.Action.COMMENTED
                text = "abc"
                user = User(13, "Wilbur")
            }
        ))

        val noteApi = TestListBasedNotesApi(listOf(note1))
        val dl = OsmNotesDownloader(noteApi, osmNoteQuestController, preferences, OsmNoteQuestType(), avatarsDownloader)
        dl.download(BoundingBox(0.0, 0.0, 1.0, 1.0), 0, 1000)

        verify(avatarsDownloader).download(setOf(54, 13))
    }
}

private fun createANote(id: Long): Note {
    val note = Note()
    note.id = id
    note.position = OsmLatLon(6.0, 7.0)
    note.status = Note.Status.OPEN
    note.dateCreated = Date()
    val comment = NoteComment()
    comment.date = Date()
    comment.action = NoteComment.Action.OPENED
    comment.text = "hurp durp via " + ApplicationConstants.NAME
    note.comments.add(comment)
    return note
}

private class TestListBasedNotesApi(val notes: List<Note>) :  NotesApi(null) {
    override fun getAll(bounds: BoundingBox, handler: Handler<Note>, limit: Int, hideClosedNoteAfter: Int) {
        for (note in notes) {
            handler.handle(note)
        }
    }
}
