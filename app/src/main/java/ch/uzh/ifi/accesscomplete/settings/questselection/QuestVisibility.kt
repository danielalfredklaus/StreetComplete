package ch.uzh.ifi.accesscomplete.settings.questselection

import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType

data class QuestVisibility(val questType: QuestType<*>, var visible:Boolean) {
    val isInteractionEnabled get() = questType !is OsmNoteQuestType
}
