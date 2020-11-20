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

package ch.uzh.ifi.accesscomplete.quests.crossing_type

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AImageListQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.view.image_select.Item

class AddCrossingTypeForm : AImageListQuestAnswerFragment<String, String>() {

    override val items = listOf(
        Item("traffic_signals", R.drawable.crossing_type_signals, R.string.quest_crossing_type_signals),
        Item("uncontrolled", R.drawable.crossing_type_zebra, R.string.quest_crossing_type_uncontrolled),
        Item("unmarked", R.drawable.crossing_type_unmarked, R.string.quest_crossing_type_unmarked)
    )

    override val itemsPerRow = 3

    override fun onClickOk(selectedItems: List<String>) {
        applyAnswer(selectedItems.single())
    }
}
