/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
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

package ch.uzh.ifi.accesscomplete.view.dialogs

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import ch.uzh.ifi.accesscomplete.R

class TimePickerDialog(
    context: Context,
    initialHourOfDay: Int,
    initialMinute: Int,
    is24HourView: Boolean,
    private val callback: (hourOfDay: Int, minute: Int) -> Unit
) : AlertDialog(context, R.style.Theme_Bubble_Dialog) {

    private val timePicker: TimePicker = TimePicker(context)

    init {
        timePicker.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        setView(timePicker)
        setButton(BUTTON_POSITIVE,context.getString(android.R.string.ok)) { _, _ ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P || timePicker.validateInput()) {
                callback(timePicker.hour, timePicker.minute)
                // Clearing focus forces the dialog to commit any pending
                // changes, e.g. typed text in a NumberPicker.
                timePicker.clearFocus()
                dismiss()
            }
        }
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel)) { _, _ ->
            cancel()
        }
        timePicker.setIs24HourView(is24HourView)
        timePicker.hour = initialHourOfDay
        timePicker.minute = initialMinute
    }

    fun updateTime(hourOfDay: Int, minuteOfHour: Int) {
        timePicker.hour = hourOfDay
        timePicker.minute = minuteOfHour
    }

    override fun onSaveInstanceState(): Bundle {
        val state = super.onSaveInstanceState()
        state.putInt(HOUR, timePicker.hour)
        state.putInt(MINUTE, timePicker.minute)
        state.putBoolean(IS_24_HOUR, timePicker.is24HourView)
        return state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val hour = savedInstanceState.getInt(HOUR)
        val minute = savedInstanceState.getInt(MINUTE)
        timePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR))
        timePicker.hour = hour
        timePicker.minute = minute
    }

    companion object {
        private const val HOUR = "hour"
        private const val MINUTE = "minute"
        private const val IS_24_HOUR = "is24hour"
    }
}
