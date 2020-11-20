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
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.view.children

import ch.uzh.ifi.accesscomplete.R

typealias RangePickedCallback = (startIndex: Int, endIndex: Int) -> Unit

/** A dialog in which you can select a range of values  */
class RangePickerDialog(
    context: Context,
    values: Array<String>,
    startIndex: Int?,
    endIndex: Int?,
    title: CharSequence,
    private val callback: RangePickedCallback
) : AlertDialog(context, R.style.Theme_Bubble_Dialog) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_range_picker, null)
        setView(view)
        setTitle(title)

        val startPicker = view.findViewById<NumberPicker>(R.id.startPicker)
        val endPicker = view.findViewById<NumberPicker>(R.id.endPicker)

        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok)) { _, _ ->
            callback(startPicker.value, endPicker.value)
            dismiss()
        }
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel)) { _, _ ->
            cancel()
        }

        startPicker.wrapSelectorWheel = false
        startPicker.displayedValues = values
        startPicker.minValue = 0
        startPicker.maxValue = values.size - 1
        startPicker.value = startIndex ?: 0

        endPicker.wrapSelectorWheel = false
        endPicker.displayedValues = values
        endPicker.minValue = 0
        endPicker.maxValue = values.size - 1
        endPicker.value = endIndex ?: values.size - 1

        // do not allow keyboard input
        startPicker.disableEditTextsFocus()
        endPicker.disableEditTextsFocus()
    }

    private fun ViewGroup.disableEditTextsFocus() {
        for (child in children) {
            if (child is ViewGroup) {
                child.disableEditTextsFocus()
            } else if (child is EditText) {
                child.isFocusable = false
            }
        }
    }
}
