package ch.uzh.ifi.accesscomplete.quests.tactile_paving

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AYesNoQuestAnswerFragment

class TactilePavingForm : AYesNoQuestAnswerFragment<Boolean>() {

    override val contentLayoutResId = R.layout.quest_tactile_paving

    override fun onClick(answer: Boolean) { applyAnswer(answer) }

}
