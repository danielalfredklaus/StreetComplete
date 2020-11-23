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
import ch.uzh.ifi.accesscomplete.util.MultiIterable
import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element

class AddPathIncline : OsmFilterQuestType<String>() {

    override val elementFilter = """
        ways with (
            highway = footway
            or (highway ~ path|cycleway|bridleway and foot != no)
        )
        and access !~ private|no
        and foot !~ private|no|use_sidepath
        and footway != crossing
        and !level
        and (!conveying or conveying = no)
        and (!indoor or indoor = no)
        and (!area or area = no)
        and (!incline or incline older today -8 years)
    """

    // TODO sst: Remove after usability testing
    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> {
        return mapData.ways.filter { it.id == 4249706L }
    }

    // TODO sst: Remove after usability testing
    override fun isApplicableTo(element: Element) = element.id == 4249706L

    override val commitMessage = "Add incline info"
    override val wikiLink = "Key:incline"
    override val icon = R.drawable.ic_quest_incline_path
    override val indicateDirection = true
    override val isSplitWayEnabled = false

    override fun getTitle(tags: Map<String, String>) = R.string.quest_incline_path_title

    override fun createForm(): AddInclineForm = AddInclineForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("incline", answer)
    }
}
