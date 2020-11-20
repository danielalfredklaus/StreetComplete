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

import android.util.Log

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import ch.uzh.ifi.accesscomplete.data.osmnotes.ImageUploadException
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNoteWithPhotosUploader
import ch.uzh.ifi.accesscomplete.data.osmnotes.deleteImages
import ch.uzh.ifi.accesscomplete.data.upload.OnUploadedChangeListener
import ch.uzh.ifi.accesscomplete.data.upload.Uploader
import java.util.concurrent.atomic.AtomicBoolean

/** Gets all note quests from local DB and uploads them via the OSM API */
class OsmNoteQuestsChangesUploader @Inject constructor(
        private val osmNoteQuestController: OsmNoteQuestController,
        private val singleNoteUploader: OsmNoteWithPhotosUploader
): Uploader {

    override var uploadedChangeListener: OnUploadedChangeListener? = null

    /** Uploads all note quests from local DB and closes them on successful upload.
     *
     *  Drops any notes where the upload failed because of a conflict but keeps any notes where
     *  the upload failed because attached photos could not be uploaded (so it can try again
     *  later). */
    @Synchronized override fun upload(cancelled: AtomicBoolean) {
        var created = 0
        var obsolete = 0
        if (cancelled.get()) return
        for (quest in osmNoteQuestController.getAllAnswered()) {
            if (cancelled.get()) break

            try {
                val newNote = singleNoteUploader.comment(quest.note.id, quest.comment ?: "", quest.imagePaths)
                quest.note.comments = newNote.comments
                quest.note.dateClosed = newNote.dateClosed
                quest.note.status = newNote.status
                osmNoteQuestController.success(quest)

                Log.d(TAG, "Uploaded note comment ${quest.logString}")
                uploadedChangeListener?.onUploaded(NOTE, quest.center)
                created++
                deleteImages(quest.imagePaths)
            } catch (e: ConflictException) {
                osmNoteQuestController.fail(quest)

                Log.d(TAG, "Dropped note comment ${quest.logString}: ${e.message}")
                uploadedChangeListener?.onDiscarded(NOTE, quest.center)
                obsolete++
                deleteImages(quest.imagePaths)
            } catch (e: ImageUploadException) {
                Log.e(TAG, "Error uploading image attached to note comment ${quest.logString}", e)
            }
        }
        var logMsg = "Commented on $created notes"
        if (obsolete > 0) {
            logMsg += " but dropped $obsolete comments because the notes have already been closed"
        }
        Log.i(TAG, logMsg)
    }

    companion object {
        private const val TAG = "CommentNoteUpload"
        private const val NOTE = "NOTE"
    }
}

private val OsmNoteQuest.logString get() = "\"$comment\" at ${center.latitude}, ${center.longitude}"
