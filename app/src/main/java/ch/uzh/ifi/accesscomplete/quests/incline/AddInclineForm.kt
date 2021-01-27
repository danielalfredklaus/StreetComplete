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

package ch.uzh.ifi.accesscomplete.quests.incline

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.app.AlertDialog
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.numberOrNull
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestFormAnswerFragment
import ch.uzh.ifi.accesscomplete.quests.OtherAnswer
import ch.uzh.ifi.accesscomplete.util.fromDegreesToPercentage
import kotlinx.android.synthetic.main.quest_incline.*
import java.lang.NumberFormatException
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

class AddInclineForm : AbstractQuestFormAnswerFragment<String>() {

    override val contentLayoutResId = R.layout.quest_incline

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null
    private var deviceMeasurementActive = true

    override val otherAnswers = listOf(
        OtherAnswer(R.string.quest_incline_no_incline_here) { applyAnswer("0%") }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInstructions()
        initMeasurementSensors()
        checkIsFormComplete()
        initInputElements()
    }

    private fun initInstructions() {
        deviceMeasurementInstructions.text = buildInstructionTextAsOrderedList(listOf(
            resources.getText(R.string.quest_incline_device_instructions_step_1),
            resources.getText(R.string.quest_incline_device_instructions_step_2),
            resources.getText(R.string.quest_incline_device_instructions_step_3),
            resources.getText(R.string.quest_incline_device_instructions_step_4)))

        manualMeasurementInstructions.text = buildInstructionTextAsOrderedList(listOf(
            resources.getText(R.string.quest_incline_manual_instructions_step_1),
            resources.getText(R.string.quest_incline_manual_instructions_step_2),
            resources.getText(R.string.quest_incline_manual_instructions_step_3)))
    }

    private fun buildInstructionTextAsOrderedList(instructions: List<CharSequence>): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        instructions.forEachIndexed { index, item ->
            builder.append(
                item,
                OrderedListSpan(70, "${index + 1}."),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return builder
    }

    private fun initMeasurementSensors() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.i("Sensor", "" + accuracy)
            }

            override fun onSensorChanged(event: SensorEvent?) {
                val g = convertFloatsToDoubles(event!!.values.clone())
                val norm = sqrt(g!![0] * g[0] + g[1] * g[1] + g[2] * g[2] + g[3] * g[3])
                g[0] /= norm
                g[1] /= norm
                g[2] /= norm
                g[3] /= norm

                // Set values to quaternion letter representatives
                val x = g[0]
                val y = g[1]
                val z = g[2]
                val w = g[3]

                // Calculate Pitch in degrees (-180 to 180)
                val sinP = 2.0 * (w * x + y * z)
                val cosP = 1.0 - 2.0 * (x * x + y * y)
                val pitch = atan2(sinP, cosP) * (180 / Math.PI)

                if (!inclineView.locked) {
                    inclineView.changeIncline(pitch)
                }
            }

            private fun convertFloatsToDoubles(input: FloatArray?): DoubleArray? {
                if (input == null) return null
                val output = DoubleArray(input.size)
                for (i in input.indices) output[i] = input[i].toDouble()
                return output
            }
        }
    }

    private fun initInputElements() {
        toggleMeasurementButton.setOnClickListener {
            deviceMeasurementActive = !deviceMeasurementActive
            if (deviceMeasurementActive) {
                deviceMeasurementLayout.visibility = View.VISIBLE
                manualInputLayout.visibility = View.GONE
                toggleMeasurementButton.text = resources.getText(R.string.enter_manually)
                deviceMeasurementInstructions.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            } else {
                deviceMeasurementLayout.visibility = View.GONE
                manualInputLayout.visibility = View.VISIBLE
                toggleMeasurementButton.text = resources.getText(R.string.use_device_to_measure)
                manualMeasurementInstructions.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
            inclineView.changeLock(false)
            manualInputField.text = null
            checkIsFormComplete()
        }

        inclineView.addListener(object : InclineView.Listener {
            override fun onLockChanged(locked: Boolean) {
                checkIsFormComplete()
            }
        })

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

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(sensorEventListener, sensor, 100_000)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorEventListener)
    }

    override fun isFormComplete(): Boolean {
        return if (deviceMeasurementActive) {
            inclineView.locked
        } else {
            manualInputField.text.isNotEmpty()
        }
    }

    override fun onClickOk() {
        var inclineValue: Int? = null
        if (deviceMeasurementActive) {
            inclineValue = inclineView.inclineInDegrees.fromDegreesToPercentage().roundToInt()
        } else {
            val input = manualInputField.text.toString()
            try {
                inclineValue = Integer.parseInt(input)
                inclineValue = if (radioDownwardSlope.isSelected) inclineValue * -1 else inclineValue
            } catch (e: NumberFormatException) {
                manualInputField.text = null
                manualInputField.numberOrNull
            }
        }

        if (inclineValue == null) {
            isFormComplete()
            return
        } else if (inclineValue.absoluteValue > 35) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.quest_generic_confirmation_title)
                .setMessage(requireContext().getString(R.string.quest_incline_implausible_value_message, inclineValue))
                .setPositiveButton(R.string.quest_leave_not) { _, _ -> composeNote() }
                .setNegativeButton(R.string.quest_generic_confirmation_no, null)
                .setCancelable(true)
                .show()
            return
        }
        val answer = if (inclineValue < 0) "-${inclineValue.absoluteValue}%" else "$inclineValue%"
        confirmDirectionBeforeCompletion(answer)
    }

    private fun confirmDirectionBeforeCompletion(answer : String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.quest_generic_confirmation_title)
            .setMessage(requireContext().getString(R.string.quest_incline_confirm_direction_message))
            .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ -> applyAnswer(answer) }
            .setNegativeButton(R.string.quest_generic_confirmation_no, null)
            .setCancelable(true)
            .show()
    }
}
