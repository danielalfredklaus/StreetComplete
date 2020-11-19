package ch.uzh.ifi.accesscomplete.quests

class YesNoQuestAnswerFragment : AYesNoQuestAnswerFragment<Boolean>() {

    override fun onClick(answer: Boolean) { applyAnswer(answer) }
}
