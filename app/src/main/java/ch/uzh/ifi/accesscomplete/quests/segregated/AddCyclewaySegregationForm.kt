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

package ch.uzh.ifi.accesscomplete.quests.segregated

import android.os.Bundle
import android.view.View

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AImageListQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.view.image_select.Item

class AddCyclewaySegregationForm : AImageListQuestAnswerFragment<Boolean, Boolean>() {

    override val items get() = listOf(
        Item(true, if (countryInfo.isLeftHandTraffic) R.drawable.ic_path_segregated_l else R.drawable.ic_path_segregated, R.string.quest_segregated_separated),
        Item(false, R.drawable.ic_path_segregated_no, R.string.quest_segregated_mixed)
    )

    override val itemsPerRow = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageSelector.cellLayoutId  = R.layout.cell_labeled_icon_select_right
    }

    override fun onClickOk(selectedItems: List<Boolean>) {
        applyAnswer(selectedItems.single())
    }
}
