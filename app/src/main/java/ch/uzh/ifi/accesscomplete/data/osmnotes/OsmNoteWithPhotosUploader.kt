package ch.uzh.ifi.accesscomplete.data.osmnotes

import android.util.Log
import ch.uzh.ifi.accesscomplete.data.NotesApi
import de.westnordost.osmapi.common.errors.OsmConflictException
import de.westnordost.osmapi.common.errors.OsmNotFoundException
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.notes.Note
import ch.uzh.ifi.accesscomplete.data.osm.upload.ConflictException
import javax.inject.Inject

/** Uploads a new note or a note comment to OSM, with the option to attach a number of photos */
class OsmNoteWithPhotosUploader @Inject constructor(
    private val notesApi: NotesApi,
    private val imageUploader: StreetCompleteImageUploader
) {

    /** Creates a new note
     *
     * @throws ImageUploadException if any attached photo could not be uploaded
     */
    fun create(pos: LatLon, text: String, imagePaths: List<String>?): Note {
        val attachedPhotosText = uploadAndGetAttachedPhotosText(imagePaths)
        val note = notesApi.create(pos, text + attachedPhotosText)
        if (!imagePaths.isNullOrEmpty()) {
            activateImages(note.id)
        }
        return note
    }

    /** Comments on an existing note
     *
     * @throws ImageUploadException if any attached photo could not be uploaded
     * @throws ConflictException if the note has already been closed or deleted
     */
    fun comment(noteId: Long, text: String, imagePaths: List<String>?): Note {
        try {
            val attachedPhotosText = uploadAndGetAttachedPhotosText(imagePaths)
            val note = notesApi.comment(noteId, text + attachedPhotosText)
            if (!imagePaths.isNullOrEmpty()) {
                activateImages(note.id)
            }
            return note
        } catch (e: OsmNotFoundException) {
            // someone else already closed the note -> our contribution is probably worthless
            throw ConflictException(e.message, e)
        } catch (e: OsmConflictException) {
            throw ConflictException(e.message, e)
        }
    }

    private fun activateImages(noteId: Long) {
        try {
            imageUploader.activate(noteId)
        } catch (e: ImageActivationException) {
            Log.e("OsmNoteUploader", "Image activation failed", e)
        }
    }

    private fun uploadAndGetAttachedPhotosText(imagePaths: List<String>?): String {
        if (!imagePaths.isNullOrEmpty()) {
            val urls = imageUploader.upload(imagePaths)
            if (urls.isNotEmpty()) {
                return "\n\nAttached photo(s):\n" + urls.joinToString("\n")
            }
        }
        return ""
    }
}
