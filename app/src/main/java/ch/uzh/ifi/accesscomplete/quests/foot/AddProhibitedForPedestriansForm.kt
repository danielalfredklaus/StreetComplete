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

package ch.uzh.ifi.accesscomplete.quests.foot

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.quests.OtherAnswer
import ch.uzh.ifi.accesscomplete.quests.foot.ProhibitedForPedestriansAnswer.*
import kotlinx.android.synthetic.main.quest_buttonpanel_yes_no_sidewalk.*

class AddProhibitedForPedestriansForm : AbstractQuestAnswerFragment<ProhibitedForPedestriansAnswer>() {

    override val buttonsResId = R.layout.quest_buttonpanel_yes_no_sidewalk

    override val contentLayoutResId = R.layout.quest_prohibited_for_pedestrians_separate_sidewalk_explanation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yesButton.setOnClickListener { applyAnswer(YES) }
        noButton.setOnClickListener { applyAnswer(NO) }
        sidewalkButton.setOnClickListener { applyAnswer(HAS_SEPARATE_SIDEWALK) }
    }

    // the living street answer stuff is copied from AddMaxSpeedForm
    override val otherAnswers: List<OtherAnswer> get() {
        val result = mutableListOf<OtherAnswer>()

        val highwayTag = osmElement!!.tags["highway"]!!
        if (countryInfo.isLivingStreetKnown && MAYBE_LIVING_STREET.contains(highwayTag)) {
            result.add(OtherAnswer(R.string.quest_maxspeed_answer_living_street) { confirmLivingStreet() })
        }
        return result
    }

    private fun confirmLivingStreet() {
        activity?.let {
            val view = layoutInflater.inflate(R.layout.quest_maxspeed_living_street_confirmation, null, false)
            // this is necessary because the inflated image view uses the activity context rather than
            // the fragment / layout inflater context' resources to access it's drawable
            val img = view.findViewById<ImageView>(R.id.livingStreetImage)
            img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_living_street))
            AlertDialog.Builder(it)
                .setView(view)
                .setTitle(R.string.quest_maxspeed_answer_living_street_confirmation_title)
                .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ -> applyAnswer(IS_LIVING_STREET) }
                .setNegativeButton(R.string.quest_generic_confirmation_no, null)
                .show()
        }
    }

    companion object {
        private val MAYBE_LIVING_STREET = listOf("residential", "unclassified")
    }
}
