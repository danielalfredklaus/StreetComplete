package de.westnordost.accesscomplete.quests

import de.westnordost.accesscomplete.data.quest.QuestGroup

interface IsShowingQuestDetails {
    val questId: Long
    val questGroup: QuestGroup
}
