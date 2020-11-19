package ch.uzh.ifi.accesscomplete.data.osmnotes.notequests

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.quests.note_discussion.NoteAnswer
import ch.uzh.ifi.accesscomplete.quests.note_discussion.NoteDiscussionForm

class OsmNoteQuestType : QuestType<NoteAnswer> {
    override val icon = R.drawable.ic_quest_notes
    override val title = R.string.quest_noteDiscussion_title

    override fun createForm() = NoteDiscussionForm()
}
