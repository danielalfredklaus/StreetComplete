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

package ch.uzh.ifi.accesscomplete.quests

import android.os.Bundle
import android.view.View

import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.quest_buttonpanel_yes_no.*

/** Abstract base class for dialogs in which the user answers a yes/no quest  */
abstract class AYesNoQuestAnswerFragment<T> : AbstractQuestAnswerFragment<T>() {

    override val buttonsResId = R.layout.quest_buttonpanel_yes_no

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yesButton.setOnClickListener { onClick(true) }
        noButton.setOnClickListener { onClick(false) }
    }

    protected abstract fun onClick(answer: Boolean)
}
