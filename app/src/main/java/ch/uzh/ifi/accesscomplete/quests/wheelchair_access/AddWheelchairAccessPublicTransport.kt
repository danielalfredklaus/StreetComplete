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

package ch.uzh.ifi.accesscomplete.quests.wheelchair_access

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder

class AddWheelchairAccessPublicTransport : OsmFilterQuestType<String>() {

    override val elementFilter = """
        nodes, ways, relations with (amenity = bus_station or railway ~ station|subway_entrance)
        and (
          !wheelchair
          or wheelchair != yes and wheelchair older today -4 years
          or wheelchair older today -8 years
        )
    """
    override val commitMessage = "Add wheelchair access to public transport platforms"
    override val wikiLink = "Key:wheelchair"
    override val icon = R.drawable.ic_quest_wheelchair

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        val type: String = tags["amenity"] ?: tags["railway"] ?: ""

        return if (hasName) {
            when (type) {
                "bus_station"     -> R.string.quest_wheelchairAccess_bus_station_name_title
                "station"         -> R.string.quest_wheelchairAccess_railway_station_name_title
                "subway_entrance" -> R.string.quest_wheelchairAccess_subway_entrance_name_title
                else              -> R.string.quest_wheelchairAccess_location_name_title
            }
        } else {
            when (type) {
                "bus_station"     -> R.string.quest_wheelchairAccess_bus_station_title
                "station"         -> R.string.quest_wheelchairAccess_railway_station_title
                "subway_entrance" -> R.string.quest_wheelchairAccess_subway_entrance_title
                else              -> R.string.quest_wheelchairAccess_location_title
            }
        }
    }

    override fun createForm() = AddWheelchairAccessPublicTransportForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("wheelchair", answer)
    }
}
