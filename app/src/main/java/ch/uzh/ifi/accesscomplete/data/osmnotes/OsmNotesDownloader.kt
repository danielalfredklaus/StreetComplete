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
import android.util.Log
import ch.uzh.ifi.accesscomplete.data.NotesApi

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.Prefs
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.notes.Note
import de.westnordost.osmapi.notes.NoteComment
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuest
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType

/** Takes care of downloading notes, creating quests out of them and persisting them */
class OsmNotesDownloader @Inject constructor(
    private val notesApi: NotesApi,
    private val osmNoteQuestController: OsmNoteQuestController,
    private val preferences: SharedPreferences,
    private val questType: OsmNoteQuestType,
    private val avatarsDownloader: OsmAvatarsDownloader
) {
    fun download(bbox: BoundingBox, userId: Long, max: Int) {
        val quests = ArrayList<OsmNoteQuest>()
        val noteCommentUserIds = HashSet<Long>()

        notesApi.getAll(bbox, { note ->
            if (note.comments.isNotEmpty() && note.probablyCreatedViaApp()) {
                val quest = OsmNoteQuest(note, questType)
                if (shouldMakeNoteClosed(userId, note)) {
                    quest.status = QuestStatus.CLOSED
                } else if (shouldMakeNoteInvisible(quest)) {
                    quest.status = QuestStatus.INVISIBLE
                }
                quests.add(quest)
                for (comment in note.comments) {
                    if (comment.user != null) noteCommentUserIds.add(comment.user.id)
                }
            }
        }, max, 0)

        val update = osmNoteQuestController.replaceInBBox(quests, bbox)

        Log.i(TAG,
            "Successfully added ${update.added} new and removed ${update.deleted} closed notes" +
            " (${update.closed} of ${quests.size} notes are hidden)"
        )

        avatarsDownloader.download(noteCommentUserIds)
    }

    // the difference to hidden is that is that invisible quests may turn visible again, dependent
    // on the user's settings while hidden quests are "dead"
    private fun shouldMakeNoteInvisible(quest: OsmNoteQuest): Boolean {
        /* many notes are created to report problems on the map that cannot be resolved
         * through an on-site survey rather than questions from other (armchair) mappers
         * that want something cleared up on-site.
         * Likely, if something is posed as a question, the reporter expects someone to
         * answer/comment on it, so let's only show these */
        val showNonQuestionNotes = preferences.getBoolean(Prefs.SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS, false)
        return !(quest.probablyContainsQuestion() || showNonQuestionNotes)
    }

    private fun shouldMakeNoteClosed(userId: Long?, note: Note): Boolean {
        if (userId == null) return false
        /* hide a note if he already contributed to it. This can also happen from outside
           this application, which is why we need to overwrite its quest status. */
        return note.containsCommentFromUser(userId) || note.probablyCreatedByUserInApp(userId)
    }

    companion object {
        private const val TAG = "QuestDownload"
    }
}

private fun Note.containsCommentFromUser(userId: Long): Boolean {
    for (comment in comments) {
        val isComment = comment.action == NoteComment.Action.COMMENTED
        if (comment.isFromUser(userId) && isComment) return true
    }
    return false
}

private fun Note.probablyCreatedByUserInApp(userId: Long): Boolean {
    val firstComment = comments.first()
    return firstComment.isFromUser(userId) && probablyCreatedViaApp()
}

private fun Note.probablyCreatedViaApp(): Boolean {
    val firstComment = comments.first()
    return firstComment.text.contains("via " + ApplicationConstants.NAME)
}

private fun NoteComment.isFromUser(userId: Long): Boolean {
    return user != null && user.id == userId
}
