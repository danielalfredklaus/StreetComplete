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
import ch.uzh.ifi.accesscomplete.ktx.popIn
import ch.uzh.ifi.accesscomplete.ktx.popOut
import ch.uzh.ifi.accesscomplete.ktx.toast
import kotlinx.android.synthetic.main.fragment_quest_answer.*

/**
 * Abstract base class for dialogs in which the user answers a quest with a form he has to fill
 * out
 * */
abstract class AbstractQuestFormAnswerFragment<T> : AbstractQuestAnswerFragment<T>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        okButton.setOnClickListener {
            if (!isFormComplete()) {
                activity?.toast(R.string.no_changes)
            } else {
                onClickOk()
            }
        }
    }

    protected fun checkIsFormComplete() {
        if (isFormComplete()) {
            okButton.popIn()
        } else {
            okButton.popOut()
        }
    }

    protected abstract fun onClickOk()

    abstract fun isFormComplete(): Boolean

    override fun isRejectingClose() = isFormComplete()
}
