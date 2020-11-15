package de.westnordost.streetcomplete.data.osm.osmquest

import de.westnordost.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Way
import de.westnordost.streetcomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression

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

    private val pedestrianAccessibleWayFilter by lazy {
        """
            ways with (
                (highway !~ ${ACCEPTED_HIGHWAYS_WITHOUT_SIDEWALK_INFO.joinToString("|")} and sidewalk ~ left|right|both)
                or (highway ~ ${ACCEPTED_HIGHWAYS_WITHOUT_SIDEWALK_INFO.joinToString("|")}))
        """.toElementFilterExpression()
    }

    private val sidewalkAbsenceFilter by lazy {
        """
            ways with sidewalk !~ left|right|both
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
        val candidates = mapData.ways
            .filter { getBaseFilterExpression().matches(it) }
            .filter { pedestrianAccessibleWayFilter.matches(it) }

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
            return osmKeyAbsenceFilter.matches(element)
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
        private val ACCEPTED_HIGHWAYS_WITHOUT_SIDEWALK_INFO = arrayOf(
            "footway", "pedestrian", "cycleway", "living_street", "track", "bridleway"
        )
    }
}
