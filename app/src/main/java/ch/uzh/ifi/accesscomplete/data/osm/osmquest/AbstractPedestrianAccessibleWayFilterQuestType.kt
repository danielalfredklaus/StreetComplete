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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Way
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementFilterExpression
import ch.uzh.ifi.accesscomplete.data.elementfilter.toElementFilterExpression
import ch.uzh.ifi.accesscomplete.quests.smoothness.AddPathSmoothness
import ch.uzh.ifi.accesscomplete.quests.smoothness.AddPedestrianAccessibleStreetSmoothness
import ch.uzh.ifi.accesscomplete.quests.surface.AddPathSurface
import ch.uzh.ifi.accesscomplete.quests.surface.AddPedestrianAccessibleStreetSurface
import ch.uzh.ifi.accesscomplete.quests.width.AddPathWidth
import ch.uzh.ifi.accesscomplete.quests.width.AddPedestrianAccessibleStreetWidth

/**
 * This abstract quest type can be used if quests should only appear on ways that are accessible to
 * pedestrians. Whenever the highway tag indicates that a way can only be used by pedestrians if it
 * has a sidewalk, a quest may only be created if the way specifies the existence of a sidewalk that
 * is not mapped separately from this way.
 *
 * The filter mechanism requires that the quest is only interested in mapping a single OSM key. If
 * needed, the existence of the specified OSM key will be checked on each sidewalk side.
 */
abstract class AbstractPedestrianAccessibleWayFilterQuestType<T> : OsmElementQuestType<T> {

    // Filter ways that can be used by pedestrians via a sidewalk that is not mapped separately or
    // by using the way itself (depending on the value of the highway tag). If ways are tagged with
    // "sidewalk = yes" they are discarded, because it is unknown for which sides the user needs
    // to answer questions. Ways that are tagged like this should first be updated with another quest.
    private val pedestrianAccessibleWayFilter by lazy {
        """
            ways with (
                (sidewalk ~ left|right|both)
                or (highway ~ ${ASSUMED_PEDESTRIAN_ACCESSIBLE_HIGHWAYS.joinToString("|")})
                or (highway ~ ${ASSUMED_PEDESTRIAN_ACCESSIBLE_HIGHWAYS_IF_NO_SIDEWALK.joinToString("|")} and sidewalk ~ no|none)
            )
            and access !~ private|no
            and foot !~ private|no|use_sidepath
            and sidewalk !~ separate|use_sidepath|yes
        """.toElementFilterExpression()
    }

    private val sidewalkAbsenceFilter by lazy {
        """
            ways with sidewalk !~ left|right|both|yes
        """.toElementFilterExpression()
    }

    private val osmKeyAbsenceFilter by lazy {
        """
            ways with (!${getOsmKey()} or ${getOsmKey()} ${getResurveyCondition()})
        """.toElementFilterExpression()
    }

    private val sidewalkLeftOsmKeyAbsenceFilter by lazy {
        """
            ways with (!${getSidewalkLeftOsmKey()} or ${getSidewalkLeftOsmKey()}  ${getResurveyCondition()})
        """.toElementFilterExpression()
    }

    private val sidewalkRightOsmKeyAbsenceFilter by lazy {
        """
            ways with (!${getSidewalkRightOsmKey()} or ${getSidewalkRightOsmKey()} ${getResurveyCondition()})
        """.toElementFilterExpression()
    }

    private val sidewalkBothOsmKeyAbsenceFilter by lazy {
        """
            ways with (
                (!${getSidewalkBothOsmKey()} or ${getSidewalkBothOsmKey()} ${getResurveyCondition()})
                and (
                    (!${getSidewalkLeftOsmKey()} or ${getSidewalkLeftOsmKey()} ${getResurveyCondition()})
                    or (!${getSidewalkRightOsmKey()} or ${getSidewalkRightOsmKey()} ${getResurveyCondition()})))
        """.toElementFilterExpression()
    }

    abstract fun getBaseFilterExpression(): ElementFilterExpression

    abstract fun supportTaggingBySidewalkSide(): Boolean

    abstract fun getOsmKey(): String

    protected fun getSidewalkLeftOsmKey(): String = "sidewalk:left:${getOsmKey()}"

    protected fun getSidewalkRightOsmKey(): String = "sidewalk:right:${getOsmKey()}"

    protected fun getSidewalkBothOsmKey(): String = "sidewalk:both:${getOsmKey()}"

    protected fun getResurveyCondition(): String = "older today -8 years"

    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> {
        // TODO sst: Remove after usability testing
        if (this is AddPathSurface || this is AddPathSmoothness || this is AddPathWidth) {
            return mapData.ways
                .filter { it.id == 39984121L }
        } else if (this is AddPedestrianAccessibleStreetSmoothness
            || this is AddPedestrianAccessibleStreetSurface
            || this is AddPedestrianAccessibleStreetWidth) {
            return mapData.ways
                .filter { it.id == 12541930L }
        }

        val candidates = mapData.ways
            .filter { getBaseFilterExpression().matches(it) }
            .filter { pedestrianAccessibleWayFilter.matches(it) }
            .toMutableSet()

        // TODO sst: Remove after usability testing
        candidates.clear()

        if (!supportTaggingBySidewalkSide()) {
            return candidates
                .filter { sidewalkAbsenceFilter.matches(it) }
                .filter { osmKeyAbsenceFilter.matches(it) }
        }

        val waysWithNoSidewalk = mutableSetOf<Way>()
        val waysWithSidewalkLeft = mutableSetOf<Way>()
        val waysWithSidewalkRight = mutableSetOf<Way>()
        val waysWithSidewalkBoth = mutableSetOf<Way>()

        candidates.forEach { way ->
            when {
                hasSidewalkLeft(way.tags) -> waysWithSidewalkLeft.add(way)
                hasSidewalkRight(way.tags) -> waysWithSidewalkRight.add(way)
                hasSidewalkBoth(way.tags) -> waysWithSidewalkBoth.add(way)
                else -> waysWithNoSidewalk.add(way)
            }
        }

        val applicableElements = mutableSetOf<Way>()
        applicableElements.addAll(waysWithNoSidewalk.filter { osmKeyAbsenceFilter.matches(it) })
        applicableElements.addAll(waysWithSidewalkLeft.filter { sidewalkLeftOsmKeyAbsenceFilter.matches(it) })
        applicableElements.addAll(waysWithSidewalkRight.filter { sidewalkRightOsmKeyAbsenceFilter.matches(it) })
        applicableElements.addAll(waysWithSidewalkBoth.filter { sidewalkBothOsmKeyAbsenceFilter.matches(it) })

        return applicableElements
    }

    override fun isApplicableTo(element: Element): Boolean? {
        if (element !is Way || !getBaseFilterExpression().matches(element) || !pedestrianAccessibleWayFilter.matches(element)) {
            return false
        }

        if (!supportTaggingBySidewalkSide()) {
            return osmKeyAbsenceFilter.matches(element) && (element.id == 39984121L || element.id == 12541930L)// TODO sst: Remove later condition after usability testing
        }

        return when {
            hasSidewalkLeft(element.tags) -> sidewalkLeftOsmKeyAbsenceFilter.matches(element)
            hasSidewalkRight(element.tags) -> sidewalkRightOsmKeyAbsenceFilter.matches(element)
            hasSidewalkBoth(element.tags) -> sidewalkBothOsmKeyAbsenceFilter.matches(element)
            else -> osmKeyAbsenceFilter.matches(element)
        }
    }

    protected fun hasSidewalk(tags: Map<String, String>): Boolean {
        return hasSidewalkLeft(tags) || hasSidewalkRight(tags) || hasSidewalkBoth(tags)
    }

    private fun hasSidewalkLeft(tags: Map<String, String>): Boolean {
        return "left" == tags["sidewalk"]
    }

    private fun hasSidewalkRight(tags: Map<String, String>): Boolean {
        return "right" == tags["sidewalk"]
    }

    private fun hasSidewalkBoth(tags: Map<String, String>): Boolean {
        return "both" == tags["sidewalk"]
    }

    companion object {
        // For the following values of the highway tag, there needs to be no info about the
        // existence of a sidewalk in order to be applicable for a quest.
        private val ASSUMED_PEDESTRIAN_ACCESSIBLE_HIGHWAYS = arrayOf(
            "footway", "pedestrian", "cycleway", "living_street", "track", "bridleway", "service"
        )

        private val ASSUMED_PEDESTRIAN_ACCESSIBLE_HIGHWAYS_IF_NO_SIDEWALK = arrayOf(
            "residential", "road", "unclassified"
        )
    }
}
