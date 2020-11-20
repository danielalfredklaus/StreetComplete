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
import de.westnordost.osmapi.common.SingleElementHandler
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.notes.Note
import ch.uzh.ifi.accesscomplete.ApplicationConstants.USER_AGENT
import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNoteWithPhotosUploader
import java.util.*
import javax.inject.Inject

/** Uploads a single note or note comment, depending if a note at the given position, referencing
 *  the same element already exists or not */
class SingleCreateNoteUploader @Inject constructor(
    private val uploader: OsmNoteWithPhotosUploader,
    private val notesApi: NotesApi
) {
    /** Creates a new note or if a note at this exact position and for this element already exists,
     *  instead adds a comment to the existing note
     *
     * @throws ImageUploadException if any attached photo could not be uploaded
     * @throws ConflictException if a note has already been created for this element but that note
     *                           has is now closed
     */
    fun upload(n: CreateNote): Note {
        if (n.elementKey != null) {
            val oldNote = findAlreadyExistingNoteWithSameAssociatedElement(n)
            if (oldNote != null) {
                return uploader.comment(oldNote.id, n.text, n.imagePaths)
            }
        }
        return uploader.create(n.position, n.fullNoteText, n.imagePaths)
    }

    private fun findAlreadyExistingNoteWithSameAssociatedElement(newNote: CreateNote): Note? {
        val handler = object : SingleElementHandler<Note>() {
            override fun handle(oldNote: Note) {
                val newNoteRegex = newNote.associatedElementRegex
                if (newNoteRegex != null) {
                    val firstCommentText = oldNote.comments[0].text
                    if (firstCommentText.matches(newNoteRegex.toRegex())) {
                        super.handle(oldNote)
                    }
                }
            }
        }
        val bbox = BoundingBox(
            newNote.position.latitude, newNote.position.longitude,
            newNote.position.latitude, newNote.position.longitude
        )
        val hideClosedNoteAfter = 7
        notesApi.getAll(bbox, handler, 10, hideClosedNoteAfter)
        return handler.get()
    }
}

private val CreateNote.fullNoteText: String get() {
    return if (elementKey != null) {
        val title = questTitle
        if (title != null) {
            "Unable to answer \"$title\" for $associatedElementString via $USER_AGENT:\n\n$text"
        } else {
            "for $associatedElementString via $USER_AGENT:\n\n$text" // TODO sst: change USER_AGENT name here?
        }
    } else "$text\n\nvia $USER_AGENT"
}

private val CreateNote.associatedElementRegex: String? get() {
    val elementKey = elementKey ?: return null
    val elementTypeName = elementKey.elementType.name
    val elementId = elementKey.elementId
    // before 0.11 - i.e. "way #123"
    val oldStyleRegex = "$elementTypeName\\s*#$elementId"
    // i.e. www.openstreetmap.org/way/123
    val newStyleRegex = "(osm|openstreetmap)\\.org\\/$elementTypeName\\/$elementId"
    // i: turns on case insensitive regex, s: newlines are also captured with "."
    return "(?is).*(($oldStyleRegex)|($newStyleRegex)).*"
}

private val CreateNote.associatedElementString: String? get() {
    val elementKey = elementKey ?: return null
    val lowercaseTypeName = elementKey.elementType.name.toLowerCase(Locale.UK)
    val elementId = elementKey.elementId
    return "https://osm.org/$lowercaseTypeName/$elementId"
}
