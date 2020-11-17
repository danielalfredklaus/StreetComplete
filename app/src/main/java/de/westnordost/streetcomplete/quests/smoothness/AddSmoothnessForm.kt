package de.westnordost.streetcomplete.quests.smoothness

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.quests.AbstractQuestFormAnswerWithSidewalkSupportFragment
import de.westnordost.streetcomplete.view.image_select.Item
import kotlinx.android.synthetic.main.quest_smoothness.*

class AddSmoothnessForm : AbstractQuestFormAnswerWithSidewalkSupportFragment<AbstractSmoothnessAnswer>() {

    override val contentLayoutResId = R.layout.quest_smoothness

    private val valueItems = listOf(
        Item("impassable", R.drawable.smoothness_impassable, R.string.quest_smoothness_impassable, R.string.quest_smoothness_impassable_description, null),
        Item("very_horrible", R.drawable.smoothness_very_horrible, R.string.quest_smoothness_very_horrible, R.string.quest_smoothness_very_horrible_description, null),
        Item("horrible", R.drawable.smoothness_horrible, R.string.quest_smoothness_horrible, R.string.quest_smoothness_horrible_description, null),
        Item("very_bad", R.drawable.smoothness_very_bad, R.string.quest_smoothness_very_bad, R.string.quest_smoothness_very_bad_description, null),
        Item("bad", R.drawable.smoothness_bad, R.string.quest_smoothness_bad, R.string.quest_smoothness_bad_description, null),
        Item("intermediate", R.drawable.smoothness_intermediate, R.string.quest_smoothness_intermediate, R.string.quest_smoothness_intermediate_description, null),
        Item("good", R.drawable.smoothness_good, R.string.quest_smoothness_good, R.string.quest_smoothness_good_description, null),
        Item("excellent", R.drawable.smoothness_excellent, R.string.quest_smoothness_excellent, R.string.quest_smoothness_excellent_description, null))
    private val initialValueIndex = 5

    private var answer: AbstractSmoothnessAnswer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSlider()
        checkIsFormComplete()
    }

    private fun initSlider() {
        valueSlider.apply {
            valueFrom = 0f
            valueTo = valueItems.size.toFloat() - 1
            value = initialValueIndex.toFloat()
        }

        setValueInformation(valueItems[initialValueIndex])
        valueSlider.addOnChangeListener { _, value, _ ->
            val item = valueItems[value.toInt()]
            setValueInformation(item)
        }
    }

    private fun setValueInformation(item: Item<String>) {
        valueName.text = resources.getText(item.titleId!!)
        valueDescription.text = resources.getText(item.descriptionId!!)

        val exampleImage: Drawable? = ContextCompat.getDrawable(requireContext(), item.drawableId!!)
        valueExampleImage.setImageDrawable(exampleImage)
    }

    override fun isFormComplete(): Boolean = true

    override fun isRejectingClose(): Boolean = false

    override fun shouldTagBySidewalkSideIfApplicable() = true

    override fun getSidewalkMappedSeparatelyAnswer(): SidewalkMappedSeparatelyAnswer? {
        return SidewalkMappedSeparatelyAnswer()
    }

    override fun resetInputs() {
        valueSlider.value = initialValueIndex.toFloat()
    }

    override fun onClickOk() {
        val smoothness = valueItems[valueSlider.value.toInt()].value!!

        if (elementHasSidewalk) {
            if (answer is SidewalkSmoothnessAnswer) {
                if (currentSidewalkSide == Listener.SidewalkSide.LEFT) {
                    (answer as SidewalkSmoothnessAnswer).leftSidewalkAnswer = SimpleSmoothnessAnswer(smoothness)
                } else {
                    (answer as SidewalkSmoothnessAnswer).rightSidewalkAnswer = SimpleSmoothnessAnswer(smoothness)
                }
                applyAnswer(answer!!)
            } else {
                answer =
                    if (currentSidewalkSide == Listener.SidewalkSide.LEFT)
                        SidewalkSmoothnessAnswer(SimpleSmoothnessAnswer(smoothness), null)
                    else
                        SidewalkSmoothnessAnswer(null, SimpleSmoothnessAnswer(smoothness))
                if (sidewalkOnBothSides) {
                    switchToOppositeSidewalkSide()
                } else {
                    applyAnswer(answer!!)
                }
            }
        } else {
            answer = SimpleSmoothnessAnswer(smoothness)
            applyAnswer(answer!!)
        }
    }
}
