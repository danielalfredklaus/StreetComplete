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

package ch.uzh.ifi.accesscomplete.quests.steps_ramp

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.ktx.toYesNo

class AddStepsRamp : OsmFilterQuestType<StepsRampAnswer>() {

    override val elementFilter = """
        ways with highway = steps
         and (!indoor or indoor = no)
         and access !~ private|no
         and (!conveying or conveying = no)
         and ramp != separate
         and (
           !ramp
           or (ramp = yes and !ramp:stroller and !ramp:bicycle and !ramp:wheelchair)
           or ramp = no and ramp older today -4 years
           or ramp older today -8 years
         )
    """

    override val commitMessage = "Add whether steps have a ramp"
    override val wikiLink = "Key:ramp"
    override val icon = R.drawable.ic_quest_steps_ramp
    override val isSplitWayEnabled = true

    override fun getTitle(tags: Map<String, String>) = R.string.quest_steps_ramp_title

    override fun createForm() = AddStepsRampForm()

    override fun applyAnswerTo(answer: StepsRampAnswer, changes: StringMapChangesBuilder) {
        // special tagging if the wheelchair ramp is separate
        if (answer.wheelchairRamp == WheelchairRampStatus.SEPARATE) {
            val hasAnotherRamp = answer.bicycleRamp || answer.strollerRamp
            // there is just a separate wheelchair ramp -> use ramp=separate, otherwise just yes
            changes.updateWithCheckDate("ramp", if (!hasAnotherRamp) "separate" else "yes")
            changes.applyRampAnswer("bicycle", answer.bicycleRamp)
            changes.applyRampAnswer("stroller", answer.strollerRamp)
            changes.addOrModify("ramp:wheelchair", "separate")
        } else {
            // updating ramp key: We need to take into account other ramp:*=yes values not touched
            // by this app
            val supportedRampKeys = listOf("ramp:wheelchair", "ramp:stroller", "ramp:bicycle")
            val anyUnsupportedRampTagIsYes = changes.getPreviousEntries().filterKeys {
                it.startsWith("ramp:") && !supportedRampKeys.contains(it)
            }.any { it.value != "no" }

            val hasRamp = (answer.hasRamp() || anyUnsupportedRampTagIsYes)
            changes.updateWithCheckDate("ramp", hasRamp.toYesNo())

            val hasWheelchairRamp = answer.wheelchairRamp != WheelchairRampStatus.NO
            changes.applyRampAnswer("bicycle", answer.bicycleRamp)
            changes.applyRampAnswer("stroller", answer.strollerRamp)
            changes.applyRampAnswer("wheelchair", hasWheelchairRamp)
        }
    }
}

private fun StringMapChangesBuilder.applyRampAnswer(rampType: String, hasRamp: Boolean) {
    if (hasRamp) {
        addOrModify("ramp:$rampType", "yes")
    } else if(getPreviousValue("ramp:$rampType") in listOf("yes", "separate")) {
        delete("ramp:$rampType")
    }
}
