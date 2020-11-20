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

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.ANYTHING_PAVED
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.quests.foot.ProhibitedForPedestriansAnswer.*

class AddProhibitedForPedestrians : OsmFilterQuestType<ProhibitedForPedestriansAnswer>() {

    override val elementFilter = """
        ways with (
          ~'sidewalk(:both)?' ~ none|no or
          (sidewalk:left ~ none|no and sidewalk:right ~ none|no)
        )
        and !foot
        and access !~ private|no
        """ +
        /* asking for any road without sidewalk is too much. Main interesting situations are
           certain road sections within large intersections, overpasses, underpasses,
           inner segregated lanes of large streets, connecting/linking road way sections and so
           forth. See https://lists.openstreetmap.org/pipermail/tagging/2019-February/042852.html */
        // only roads where foot=X is not (almost) implied
        "and motorroad != yes " +
        "and highway ~ trunk|trunk_link|primary|primary_link|secondary|secondary_link|tertiary|tertiary_link|unclassified " +
        // road probably not developed enough to issue a prohibition for pedestrians
        "and surface ~ ${ANYTHING_PAVED.joinToString("|")} " +
        // fuzzy filter for above mentioned situations + developed-enough / non-rural roads
        "and ( oneway~yes|-1 or bridge=yes or tunnel=yes or bicycle~no|use_sidepath or lit=yes )"

    override val commitMessage = "Add whether roads are prohibited for pedestrians"
    override val wikiLink = "Key:foot"
    override val icon = R.drawable.ic_quest_no_pedestrians
    override val isSplitWayEnabled = true

    override fun getTitle(tags: Map<String, String>) = R.string.quest_accessible_for_pedestrians_title_prohibited

    override fun createForm() = AddProhibitedForPedestriansForm()

    override fun applyAnswerTo(answer: ProhibitedForPedestriansAnswer, changes: StringMapChangesBuilder) {
        when(answer) {
            // the question is whether it is prohibited, so YES -> foot=no etc
            YES -> changes.add("foot", "no")
            NO -> changes.add("foot", "yes")
            HAS_SEPARATE_SIDEWALK -> {
                changes.add("foot", "use_sidepath")
                changes.modify("sidewalk", "separate")
            }
            IS_LIVING_STREET -> changes.modify("highway", "living_street")
        }
    }
}
