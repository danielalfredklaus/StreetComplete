package de.westnordost.accesscomplete.quests

class YesNoQuestAnswerFragment : AYesNoQuestAnswerFragment<Boolean>() {

    override fun onClick(answer: Boolean) { applyAnswer(answer) }
}
