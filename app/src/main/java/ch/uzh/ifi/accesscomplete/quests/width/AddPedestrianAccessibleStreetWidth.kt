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

package ch.uzh.ifi.accesscomplete.quests.width

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementFilterExpression
import ch.uzh.ifi.accesscomplete.data.elementfilter.toElementFilterExpression

class AddPedestrianAccessibleStreetWidth : AbstractAddWidthQuestType() {

    private val baseExpression by lazy {
        """
            ways with highway ~ ${STREETS_WITH_VALUABLE_WIDTH_INFO.joinToString("|")}
            and (!area or area = no)
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_width_street

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        val hasSidewalk = hasSidewalk(tags)

        return if (hasName) {
            when {
                hasSidewalk -> R.string.quest_width_street_name_sidewalk_title
                else -> R.string.quest_width_street_name_title
            }
        } else {
            when {
                hasSidewalk -> R.string.quest_width_street_sidewalk_title
                else -> R.string.quest_width_street_title
            }
        }
    }

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = true

    companion object {
        private val STREETS_WITH_VALUABLE_WIDTH_INFO = arrayOf(
            "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
            "unclassified", "residential", "living_street", "track", "road"

            // These streets typically do not have any sidewalks (or they are mapped separately):
            // "trunk", "trunk_link", "motorway", "motorway_link"

            // Streets that are typically very wide and are not worth measuring:
            // "pedestrian"

            // This is too much, and the information value is very low:
            // "service"
        )
    }
}
