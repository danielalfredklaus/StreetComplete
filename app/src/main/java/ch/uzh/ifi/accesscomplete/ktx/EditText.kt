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

package ch.uzh.ifi.accesscomplete.ktx

import android.text.method.DigitsKeyListener
import android.widget.EditText

/* Workaround for an Android bug that it assumes the decimal separator to always be the "."
   for EditTexts with inputType "numberDecimal", independent of Locale. See
   https://issuetracker.google.com/issues/36907764 .

   Affected Android versions are all versions till (exclusive) Android Oreo. */

fun EditText.allowOnlyNumbers() {
    keyListener = DigitsKeyListener.getInstance("0123456789,.")
}

val EditText.numberOrNull get() = text.toString().trim().replace(",", ".").toDoubleOrNull()
