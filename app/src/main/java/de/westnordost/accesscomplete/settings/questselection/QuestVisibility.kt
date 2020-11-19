package de.westnordost.accesscomplete.settings.questselection

import de.westnordost.accesscomplete.data.quest.QuestType
import de.westnordost.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType

data class QuestVisibility(val questType: QuestType<*>, var visible:Boolean) {
    val isInteractionEnabled get() = questType !is OsmNoteQuestType
}
