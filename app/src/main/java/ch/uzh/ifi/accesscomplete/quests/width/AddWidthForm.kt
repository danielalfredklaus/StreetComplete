/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.quests.width

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity.Companion.EXTRA_ADDITIONAL_INSTRUCTIONS_ID
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity.Companion.EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity.Companion.REQUEST_CODE_MEASURE_DISTANCE
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity.Companion.RESULT_ATTRIBUTE_DISTANCE
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity.Companion.checkIsSupportedDevice
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestAnswerFragment.Listener.SidewalkSide
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestFormAnswerWithSidewalkSupportFragment
import ch.uzh.ifi.accesscomplete.util.checkIfTalkBackIsActive
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
        // The ARCore measurement is not accessible for screen reader users, hence the option will
        // be removed if TalkBack is active.
        if (checkIfTalkBackIsActive(requireContext())) {
            measureButton.visibility = View.GONE
        } else {
            measureButton.setOnClickListener {
                val intent = Intent(activity?.application, ARCoreMeasurementActivity::class.java)
                intent.putExtra(EXTRA_ADDITIONAL_INSTRUCTIONS_ID, R.string.quest_width_measurement_instructions)
                intent.putExtra(EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID, R.drawable.example_width)
                startActivityForResult(intent, REQUEST_CODE_MEASURE_DISTANCE)
            }
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
