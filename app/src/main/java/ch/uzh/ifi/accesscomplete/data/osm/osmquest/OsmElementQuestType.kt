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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.quest.AllCountries
import ch.uzh.ifi.accesscomplete.data.quest.Countries
import ch.uzh.ifi.accesscomplete.data.quest.QuestType

/** Quest type where each quest refers to an OSM element */
interface OsmElementQuestType<T> : QuestType<T> {

    fun getTitleArgs(tags: Map<String, String>, featureName: Lazy<String?>): Array<String> {
        val name = tags["name"] ?: tags["brand"]
        return if (name != null) arrayOf(name) else arrayOf()
    }

    /** the commit message to be used for this quest type */
    val commitMessage: String

    val wikiLink: String? get() = null

    // the below could also go up into QuestType interface, but then they should be accounted for
    // in the respective download/upload classes as well

    /** in which countries the quest should be shown */
    val enabledInCountries: Countries get() = AllCountries

    /** returns whether the markers should be at the ends instead of the center */
    val hasMarkersAtEnds: Boolean get() = false

    /** returns whether the direction of ways should be indicated on the map when drawing the quest geometry */
    val indicateDirection: Boolean get() = false

    /** returns whether the user should be able to split the way instead */
    val isSplitWayEnabled: Boolean get() = false

    /** returns title resource for when the element has the specified [tags]. The tags are unmodifiable */
    fun getTitle(tags: Map<String, String>): Int

    override val title: Int get() = getTitle(emptyMap())

    /** return all elements within the given map data that are applicable to this quest type. */
    fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element>

    /** returns whether a quest of this quest type could be created out of the given [element]. If the
     * element alone does not suffice to find this out (but f.e. is determined by the data around
     * it), this should return null.
     *
     * The implications of returning null here is that this quest will never be created directly
     * as consequence of solving another quest and also after reverting an input, the quest will
     * not immediately pop up again.*/
    fun isApplicableTo(element: Element): Boolean?

    /** applies the data from [answer] to the given element. The element is not directly modified,
     *  instead, a map of [changes] is built */
    fun applyAnswerTo(answer: T, changes: StringMapChangesBuilder)

    @Suppress("UNCHECKED_CAST")
    fun applyAnswerToUnsafe(answer: Any, changes: StringMapChangesBuilder) {
        applyAnswerTo(answer as T, changes)
    }

    /** The quest type can clean it's metadata here, if any  */
    fun cleanMetadata() {}
}
