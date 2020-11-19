package ch.uzh.ifi.accesscomplete.quests

import ch.uzh.ifi.accesscomplete.data.quest.QuestGroup

interface IsShowingQuestDetails {
    val questId: Long
    val questGroup: QuestGroup
}
