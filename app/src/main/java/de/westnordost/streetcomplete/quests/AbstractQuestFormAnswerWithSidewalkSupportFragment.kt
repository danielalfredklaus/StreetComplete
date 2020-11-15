package de.westnordost.streetcomplete.quests

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.quests.AbstractQuestAnswerFragment.Listener.SidewalkSide
import kotlinx.android.synthetic.main.fragment_quest_answer.*

/**
 * Abstract base class for dialogs that need to handle sidewalk quests. If the the OSM element of
 * the quest is annotated with the sidewalk tag, it can ask the question for both the left and
 * the right side (if they are available).
 */
abstract class AbstractQuestFormAnswerWithSidewalkSupportFragment<T> : AbstractQuestFormAnswerFragment<T>() {

    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    protected var elementHasSidewalk = false
    protected var sidewalkOnBothSides = false
    protected var currentSidewalkSide: SidewalkSide? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (shouldTagBySidewalkSideIfApplicable()) {
            handlePossibleSidewalks()
        }
    }

    protected open fun shouldTagBySidewalkSideIfApplicable(): Boolean = false

    private fun handlePossibleSidewalks() {
        if (osmElement == null) {
            return
        }

        val sidewalkTag = osmElement!!.tags["sidewalk"]
        if (sidewalkTag == null) {
            return
        }

        when {
            sidewalkTag.contentEquals("both") -> {
                sidewalkOnBothSides = true
                currentSidewalkSide = SidewalkSide.LEFT
                okButton.text = resources.getText(R.string.next)
            }
            sidewalkTag.contentEquals("left") -> {
                currentSidewalkSide = SidewalkSide.LEFT
            }
            sidewalkTag.contentEquals("right") -> {
                currentSidewalkSide = SidewalkSide.RIGHT
            }
        }

        if (currentSidewalkSide != null) {
            elementHasSidewalk = true
            listener?.onHighlightSidewalkSide(questId, questGroup, currentSidewalkSide!!)
        }
    }

    protected fun switchToOppositeSidewalkSide() {
        if (currentSidewalkSide == null) {
            return
        }

        bottomSheetContainer.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.enter_from_right)
        )

        currentSidewalkSide =
            if (currentSidewalkSide == SidewalkSide.LEFT) SidewalkSide.RIGHT
            else SidewalkSide.LEFT
        listener?.onHighlightSidewalkSide(questId, questGroup, currentSidewalkSide!!)

        okButton.text = resources.getText(android.R.string.ok)
        resetInputs()
    }

    protected open fun resetInputs() {
        // NOP
    }
}
