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

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes

import ch.uzh.ifi.accesscomplete.R

/**
 * Preference that shows a simple number picker
 */
class NumberPickerPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreferenceCompat(context, attrs, defStyleAttr, defStyleRes) {

    private var _value: Int = 0
    var value: Int
        get() = _value
        set(v) {
            _value = v
            persistInt(v)
            notifyChanged()
        }

    var minValue: Int = DEFAULT_MIN_VALUE
        private set
    var maxValue: Int = DEFAULT_MAX_VALUE
        private set
    var step: Int = STEP
        private set

    init {
        dialogLayoutResource = R.layout.dialog_number_picker_preference

        context.withStyledAttributes(attrs, R.styleable.NumberPickerPreference) {
            minValue = getInt(R.styleable.NumberPickerPreference_minValue, DEFAULT_MIN_VALUE)
            maxValue = getInt(R.styleable.NumberPickerPreference_maxValue, DEFAULT_MAX_VALUE)
            step = getInt(R.styleable.NumberPickerPreference_step, STEP)
        }
    }

    override fun createDialog() = NumberPickerPreferenceDialog()

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val defaultInt = defaultValue as? Int ?: DEFAULT_VALUE
        _value = if (restorePersistedValue) getPersistedInt(defaultInt) else defaultInt
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int) = a.getInteger(index, DEFAULT_VALUE)

    override fun getSummary() = String.format(super.getSummary().toString(), value)

    companion object {
        private const val DEFAULT_MIN_VALUE = 1
        private const val DEFAULT_MAX_VALUE = 100
        private const val STEP = 1

        private const val DEFAULT_VALUE = 1
    }
}
