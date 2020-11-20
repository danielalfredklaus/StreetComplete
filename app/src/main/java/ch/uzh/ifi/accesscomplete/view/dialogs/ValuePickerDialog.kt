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
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.view.children

import ch.uzh.ifi.accesscomplete.R

/** A dialog in which you can select one value from a range of values  */
class ValuePickerDialog(
    context: Context,
    values: Array<String>,
    selectedIndex: Int,
    title: CharSequence,
    private val callback: (value: Int) -> Unit
) : AlertDialog(context, R.style.Theme_Bubble_Dialog) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null)
        setView(view)
        setTitle(title)

        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)

        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.ok)) { _, _ ->
            callback(numberPicker.value)
            dismiss()
        }
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel)) { _, _ ->
            cancel()
        }
        numberPicker.wrapSelectorWheel = false
        numberPicker.displayedValues = values
        numberPicker.minValue = 0
        numberPicker.maxValue = values.size - 1
        numberPicker.value = selectedIndex
        // do not allow keyboard input
        numberPicker.disableEditTextsFocus()
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
