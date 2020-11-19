package de.westnordost.accesscomplete.quests.tactile_paving

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.quests.AYesNoQuestAnswerFragment

class TactilePavingForm : AYesNoQuestAnswerFragment<Boolean>() {

    override val contentLayoutResId = R.layout.quest_tactile_paving

    override fun onClick(answer: Boolean) { applyAnswer(answer) }

}
