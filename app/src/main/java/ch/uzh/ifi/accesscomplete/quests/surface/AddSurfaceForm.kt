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

import androidx.appcompat.app.AlertDialog
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AImageListQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.view.image_select.Item

class AddSurfaceForm : AImageListQuestAnswerFragment<String, AbstractSurfaceAnswer>() {

    override val items: List<Item<String>>
        get() =
            (PAVED_SURFACES + UNPAVED_SURFACES + GROUND_SURFACES).toItems() +
                Item("paved", R.drawable.path_surface_paved, R.string.quest_surface_value_paved, null, listOf()) +
                Item("unpaved", R.drawable.path_surface_unpaved, R.string.quest_surface_value_unpaved, null, listOf()) +
                Item("ground", R.drawable.surface_ground, R.string.quest_surface_value_ground, null, listOf())

    override val itemsPerRow = 3

    private var answer: AbstractSurfaceAnswer? = null

    override fun shouldTagBySidewalkSideIfApplicable() = true

    override fun getSidewalkMappedSeparatelyAnswer(): SidewalkMappedSeparatelyAnswer? {
        return SidewalkMappedSeparatelyAnswer()
    }

    override fun resetInputs() {
        imageSelector.selectedIndices.forEach {
            imageSelector.deselect(it)
        }
    }

    override fun onClickOk(selectedItems: List<String>) {
        val value = selectedItems.single()
        if (value == "paved" || value == "unpaved") {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.quest_surface_detailed_answer_impossible_confirmation)
                .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ ->
                    run {
                        DescribeGenericSurfaceDialog(requireContext()) { description ->
                            addOrApplyAnswer(GenericSurfaceAnswer(value, description))
                        }.show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
            return
        }
        addOrApplyAnswer(SpecificSurfaceAnswer(value))
    }

    protected fun addOrApplyAnswer(surfaceAnswer: SurfaceAnswer) {
        if (elementHasSidewalk) {
            if (answer is SidewalkSurfaceAnswer) {
                if (currentSidewalkSide == Listener.SidewalkSide.LEFT) {
                    (answer as SidewalkSurfaceAnswer).leftSidewalkAnswer = surfaceAnswer
                } else {
                    (answer as SidewalkSurfaceAnswer).rightSidewalkAnswer = surfaceAnswer
                }
                applyAnswer(answer!!)
            } else {
                answer =
                    if (currentSidewalkSide == Listener.SidewalkSide.LEFT)
                        SidewalkSurfaceAnswer(surfaceAnswer, null)
                    else
                        SidewalkSurfaceAnswer(null, surfaceAnswer)
                if (sidewalkOnBothSides) {
                    switchToOppositeSidewalkSide()
                } else {
                    applyAnswer(answer!!)
                }
            }
        } else {
            answer = surfaceAnswer
            applyAnswer(answer!!)
        }
    }
}
