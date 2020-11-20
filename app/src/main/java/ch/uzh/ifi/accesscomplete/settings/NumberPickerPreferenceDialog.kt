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

package ch.uzh.ifi.accesscomplete.settings

import androidx.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.NumberPicker

import ch.uzh.ifi.accesscomplete.R

class NumberPickerPreferenceDialog : PreferenceDialogFragmentCompat() {
    private lateinit var picker: NumberPicker
    private lateinit var values: Array<String>

    private val pref: NumberPickerPreference
        get() = preference as NumberPickerPreference


    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        picker = view.findViewById(R.id.numberPicker)
        val intValues = (pref.minValue..pref.maxValue step pref.step).toList()
        values = intValues.map { "$it" }.toTypedArray()
        var index = values.indexOf(pref.value.toString())
        if(index == -1) {
            do ++index while(index < intValues.lastIndex && intValues[index] < pref.value)
        }
        picker.apply {
            displayedValues = values
            minValue = 0
            maxValue = values.size - 1
            value = index
            wrapSelectorWheel = false
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        // hackfix: The Android number picker accepts input via soft keyboard (which makes sense
        // from a UX viewpoint) but is not designed for that. By default, it does not apply the
        // input there. See http://stackoverflow.com/questions/18944997/numberpicker-doesnt-work-with-keyboard
        // A workaround is to clear the focus before saving.
        picker.clearFocus()

        if (positiveResult) {
            pref.value = values[picker.value].toInt()
        }
    }
}
