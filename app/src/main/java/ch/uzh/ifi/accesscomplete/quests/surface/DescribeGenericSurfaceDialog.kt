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

package ch.uzh.ifi.accesscomplete.quests.surface

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import ch.uzh.ifi.accesscomplete.R

class DescribeGenericSurfaceDialog(
    context: Context,
    onSurfaceDescribed: (txt: String) -> Unit
) : AlertDialog(context, R.style.Theme_Bubble_Dialog) {
    val view = LayoutInflater.from(context).inflate(R.layout.quest_surface_detailed_answer_impossible, null)
    val explanationInput = view.findViewById<EditText>(R.id.explanationInput)
    init {
        setTitle(context.resources.getString(R.string.quest_surface_detailed_answer_impossible_title))

        setButton(
            DialogInterface.BUTTON_POSITIVE,
            context.getString(android.R.string.yes)
        ) { _, _ ->
            val txt = explanationInput.text.toString().trim()

            if (txt.isEmpty()) {
                Builder(context)
                    .setMessage(R.string.quest_surface_detailed_answer_impossible_description)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            } else {
                onSurfaceDescribed(txt)
            }
        }

        setButton(
            DialogInterface.BUTTON_NEGATIVE,
            context.getString(android.R.string.cancel),
            null as DialogInterface.OnClickListener?
        )
        setView(view)
    }
}
