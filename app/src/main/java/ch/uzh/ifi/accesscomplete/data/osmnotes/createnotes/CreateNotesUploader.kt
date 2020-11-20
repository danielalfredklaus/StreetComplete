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

import android.util.Log
import ch.uzh.ifi.accesscomplete.data.MapDataApi

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.notes.Note
import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import ch.uzh.ifi.accesscomplete.data.osm.upload.ElementDeletedException
import ch.uzh.ifi.accesscomplete.data.osmnotes.*
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuest
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.data.upload.OnUploadedChangeListener
import ch.uzh.ifi.accesscomplete.data.upload.Uploader
import java.util.concurrent.atomic.AtomicBoolean

/** Gets all create notes from local DB and uploads them via the OSM API */
class CreateNotesUploader @Inject constructor(
    private val createNoteDB: CreateNoteDao,
    private val osmNoteQuestController: OsmNoteQuestController,
    private val mapDataApi: MapDataApi,
    private val questType: OsmNoteQuestType,
    private val singleCreateNoteUploader: SingleCreateNoteUploader
): Uploader {

    override var uploadedChangeListener: OnUploadedChangeListener? = null

    /** Uploads all create notes from local DB and deletes them on successful upload.
     *
     *  Drops any notes where the upload failed because of a conflict but keeps any notes where
     *  the upload failed because attached photos could not be uploaded (so it can try again
     *  later). */
    @Synchronized override fun upload(cancelled: AtomicBoolean) {
        var created = 0
        var obsolete = 0
        if (cancelled.get()) return
        Log.i(TAG, "Uploading create notes")
        for (createNote in createNoteDB.getAll()) {
            if (cancelled.get()) break

            try {
                val newNote = uploadSingle(createNote)

                // add a closed quest as a blocker so that at this location no quests are created.
                // if the note was not added, don't do this (see below) -> probably based on old data
                val noteQuest = OsmNoteQuest(newNote, questType)
                noteQuest.status = QuestStatus.CLOSED
                osmNoteQuestController.add(noteQuest)

                Log.d(TAG, "Uploaded note ${createNote.logString}")
                uploadedChangeListener?.onUploaded(NOTE, createNote.position)
                created++
                createNoteDB.delete(createNote.id!!)
                deleteImages(createNote.imagePaths)
            } catch (e: ConflictException) {
                Log.d(TAG, "Dropped note ${createNote.logString}: ${e.message}")
                uploadedChangeListener?.onDiscarded(NOTE, createNote.position)
                obsolete++
                createNoteDB.delete(createNote.id!!)
                deleteImages(createNote.imagePaths)
            } catch (e: ImageUploadException) {
                Log.e(TAG, "Error uploading image attached to note ${createNote.logString}", e)
            }
        }
        var logMsg = "Created $created notes"
        if (obsolete > 0) {
            logMsg += " but dropped $obsolete because they were obsolete already"
        }
        Log.i(TAG, logMsg)
    }

    private fun uploadSingle(n: CreateNote): Note {
        if (n.isAssociatedElementDeleted())
            throw ElementDeletedException("Associated element deleted")

        return singleCreateNoteUploader.upload(n)
    }

    private fun CreateNote.isAssociatedElementDeleted(): Boolean {
        return elementKey != null && fetchElement() == null
    }

    private fun CreateNote.fetchElement(): Element? {
        val key = elementKey ?: return null
        return when (key.elementType) {
            Element.Type.NODE -> mapDataApi.getNode(key.elementId)
            Element.Type.WAY -> mapDataApi.getWay(key.elementId)
            Element.Type.RELATION -> mapDataApi.getRelation(key.elementId)
        }
    }

    companion object {
        private const val TAG = "CreateNotesUpload"
        private const val NOTE = "NOTE"
    }
}

private val CreateNote.logString get() = "\"$text\" at ${position.latitude}, ${position.longitude}"
