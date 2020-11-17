package de.westnordost.streetcomplete.quests.width

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.measurement.ARCoreMeasurementActivity
import de.westnordost.streetcomplete.measurement.ARCoreMeasurementActivity.Companion.EXTRA_ADDITIONAL_INSTRUCTIONS_ID
import de.westnordost.streetcomplete.measurement.ARCoreMeasurementActivity.Companion.EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID
import de.westnordost.streetcomplete.measurement.ARCoreMeasurementActivity.Companion.REQUEST_CODE_MEASURE_DISTANCE
import de.westnordost.streetcomplete.measurement.ARCoreMeasurementActivity.Companion.RESULT_ATTRIBUTE_DISTANCE
import de.westnordost.streetcomplete.measurement.ARCoreMeasurementActivity.Companion.checkIsSupportedDevice
import de.westnordost.streetcomplete.quests.AbstractQuestAnswerFragment.Listener.SidewalkSide
import de.westnordost.streetcomplete.quests.AbstractQuestFormAnswerWithSidewalkSupportFragment
import kotlinx.android.synthetic.main.quest_width.*
import kotlinx.android.synthetic.main.quest_width.manualInputField
import java.lang.NumberFormatException
import kotlin.math.roundToInt

class AddWidthForm : AbstractQuestFormAnswerWithSidewalkSupportFragment<AbstractWidthAnswer>() {

    override val contentLayoutResId = R.layout.quest_width

    private var answer: AbstractWidthAnswer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInputFields()
        checkIsFormComplete()
        checkIsSupportedDevice(requireActivity()) { disableARCoreMeasurementIfNotSupported() }
    }

    private fun initInputFields() {
        measureButton.setOnClickListener {
            val intent = Intent(activity?.application, ARCoreMeasurementActivity::class.java)
            intent.putExtra(EXTRA_ADDITIONAL_INSTRUCTIONS_ID, R.string.quest_width_measurement_instructions)
            intent.putExtra(EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID, R.drawable.example_width)
            startActivityForResult(intent, REQUEST_CODE_MEASURE_DISTANCE)
        }

        manualInputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // NOP
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // NOP
            }

            override fun afterTextChanged(s: Editable?) {
                checkIsFormComplete()
            }
        })
    }

    private fun disableARCoreMeasurementIfNotSupported() {
        measureButton.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_MEASURE_DISTANCE) {
            if (resultCode == RESULT_OK) {
                val distance = data!!.getFloatExtra((RESULT_ATTRIBUTE_DISTANCE), -1f)
                if (distance >= 0) {
                    manualInputField.setText((distance * 100).roundToInt().toString())
                }
            }
        }
    }

    override fun shouldTagBySidewalkSideIfApplicable(): Boolean = true

    override fun getSidewalkMappedSeparatelyAnswer(): SidewalkMappedSeparatelyAnswer? {
        return SidewalkMappedSeparatelyAnswer()
    }

    override fun isFormComplete(): Boolean = manualInputField.text.isNotEmpty()

    override fun resetInputs() {
        manualInputField.text = null
    }

    override fun onClickOk() {
        val widthInMeters = parseWidthInMeters()
        if (widthInMeters < 0.1f || widthInMeters > 10f) {
            val messageId =
                if (currentSidewalkSide != null)
                    R.string.quest_width_implausible_value_sidewalk_message
                else
                    R.string.quest_width_implausible_value_street_message

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.quest_generic_confirmation_title)
                .setMessage(requireContext().getString(messageId, manualInputField.text))
                .setPositiveButton(R.string.quest_leave_not) { _, _ -> composeNote() }
                .setNegativeButton(R.string.quest_generic_confirmation_no, null)
                .setCancelable(true)
                .show()
            return
        }

        val simpleWidthAnswer = SimpleWidthAnswer("%.2f".format(widthInMeters) + " m")
        if (elementHasSidewalk) {
            if (answer is SidewalkWidthAnswer) {
                if (currentSidewalkSide == SidewalkSide.LEFT) {
                    (answer as SidewalkWidthAnswer).leftSidewalkAnswer = simpleWidthAnswer
                } else {
                    (answer as SidewalkWidthAnswer).rightSidewalkAnswer = simpleWidthAnswer
                }
                applyAnswer(answer!!)
            } else {
                answer =
                    if (currentSidewalkSide == SidewalkSide.LEFT)
                        SidewalkWidthAnswer(simpleWidthAnswer, null)
                    else
                        SidewalkWidthAnswer(null, simpleWidthAnswer)
                if (sidewalkOnBothSides) {
                    switchToOppositeSidewalkSide()
                } else {
                    applyAnswer(answer!!)
                }
            }
        } else {
            answer = simpleWidthAnswer
            applyAnswer(answer!!)
        }
    }

    private fun parseWidthInMeters(): Float {
        var widthValueInCm = 0
        val input = manualInputField.text.toString()
        try {
            widthValueInCm = Integer.parseInt(input)
        } catch (e: NumberFormatException) {
            manualInputField.text = null
        }

        return widthValueInCm / 100f
    }
}
