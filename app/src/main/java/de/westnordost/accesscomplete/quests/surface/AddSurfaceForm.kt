package de.westnordost.accesscomplete.quests.surface

import androidx.appcompat.app.AlertDialog
import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.quests.AImageListQuestAnswerFragment
import de.westnordost.accesscomplete.view.image_select.Item

class AddSurfaceForm : AImageListQuestAnswerFragment<String, AbstractSurfaceAnswer>() {

    override val items: List<Item<String>>
        get() =
            (PAVED_SURFACES + UNPAVED_SURFACES + GROUND_SURFACES).toItems() +
                Item("paved", R.drawable.path_surface_paved, R.string.quest_surface_value_paved, null, listOf()) +
                Item("unpaved", R.drawable.path_surface_unpaved, R.string.quest_surface_value_unpaved, null, listOf()) +
                Item("ground", R.drawable.surface_ground, R.string.quest_surface_value_ground, null, listOf())

    override val itemsPerRow = 3

    private var answer: AbstractSurfaceAnswer? = null

    override fun shouldTagBySidewalkSideIfApplicable() = true

    override fun getSidewalkMappedSeparatelyAnswer(): SidewalkMappedSeparatelyAnswer? {
        return SidewalkMappedSeparatelyAnswer()
    }

    override fun resetInputs() {
        imageSelector.selectedIndices.forEach {
            imageSelector.deselect(it)
        }
    }

    override fun onClickOk(selectedItems: List<String>) {
        val value = selectedItems.single()
        if (value == "paved" || value == "unpaved") {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.quest_surface_detailed_answer_impossible_confirmation)
                .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ ->
                    run {
                        DescribeGenericSurfaceDialog(requireContext()) { description ->
                            addOrApplyAnswer(GenericSurfaceAnswer(value, description))
                        }.show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
            return
        }
        addOrApplyAnswer(SpecificSurfaceAnswer(value))
    }

    protected fun addOrApplyAnswer(surfaceAnswer: SurfaceAnswer) {
        if (elementHasSidewalk) {
            if (answer is SidewalkSurfaceAnswer) {
                if (currentSidewalkSide == Listener.SidewalkSide.LEFT) {
                    (answer as SidewalkSurfaceAnswer).leftSidewalkAnswer = surfaceAnswer
                } else {
                    (answer as SidewalkSurfaceAnswer).rightSidewalkAnswer = surfaceAnswer
                }
                applyAnswer(answer!!)
            } else {
                answer =
                    if (currentSidewalkSide == Listener.SidewalkSide.LEFT)
                        SidewalkSurfaceAnswer(surfaceAnswer, null)
                    else
                        SidewalkSurfaceAnswer(null, surfaceAnswer)
                if (sidewalkOnBothSides) {
                    switchToOppositeSidewalkSide()
                } else {
                    applyAnswer(answer!!)
                }
            }
        } else {
            answer = surfaceAnswer
            applyAnswer(answer!!)
        }
    }
}
