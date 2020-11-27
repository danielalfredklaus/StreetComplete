/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
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

package ch.uzh.ifi.accesscomplete.quests.incline

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element

class AddPedestrianAccessibleStreetIncline : OsmFilterQuestType<String>() {

    // TODO sst: Remove the last condition after usability testing
    override val elementFilter = """
        ways with (
            (highway ~ ${ASSUMED_PEDESTRIAN_INACCESSIBLE_STREETS.joinToString("|")} and sidewalk ~ left|right|both)
            or (highway ~ ${ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS.joinToString("|")})
            or (highway ~ ${ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS_IF_NO_SIDEWALK.joinToString("|")} and sidewalk ~ no|none)
        )
        and sidewalk !~ separate|use_sidepath
        and access !~ private|no
        and foot !~ private|no|use_sidepath
        and (!area or area = no)
        and (!incline or incline older today -8 years)
        and !highway
    """

    // TODO sst: Remove after usability testing
    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> {
        return mapData.ways.filter { it.id == 12541930L }
    }

    // TODO sst: Remove after usability testing
    override fun isApplicableTo(element: Element) = element.id == 12541930L


    override val commitMessage = "Add incline info"
    override val wikiLink = "Key:incline"
    override val icon = R.drawable.ic_quest_incline_street
    override val indicateDirection = true
    override val isSplitWayEnabled = false

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        return if (hasName)
                R.string.quest_incline_street_name_title
            else
                R.string.quest_incline_street_title
    }

    override fun createForm(): AddInclineForm = AddInclineForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("incline", answer)
    }

    companion object {
        // For the following values of the highway tag, there needs to be no info about the
        // existence of a sidewalk in order to be applicable for a quest.
        private val ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS = arrayOf(
            "pedestrian", "living_street"
        )

        private val ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS_IF_NO_SIDEWALK = arrayOf(
            "residential", "road", "unclassified"
        )

        private val ASSUMED_PEDESTRIAN_INACCESSIBLE_STREETS = arrayOf(
            "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
            "unclassified", "residential", "track",

            // These roads are typically never used by pedestrians and do not have sidewalks
            // (or they are mapped separately):
            // "trunk", "trunk_link", "motorway", "motorway_link",

            // This is too much, and the information value is very low:
            // "service"
        )
    }
}
