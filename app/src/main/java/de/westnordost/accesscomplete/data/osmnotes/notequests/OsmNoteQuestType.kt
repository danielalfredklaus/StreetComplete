package de.westnordost.accesscomplete.data.osmnotes.notequests

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.quest.QuestType
import de.westnordost.accesscomplete.quests.note_discussion.NoteAnswer
import de.westnordost.accesscomplete.quests.note_discussion.NoteDiscussionForm

class OsmNoteQuestType : QuestType<NoteAnswer> {
    override val icon = R.drawable.ic_quest_notes
    override val title = R.string.quest_noteDiscussion_title

    override fun createForm() = NoteDiscussionForm()
}
