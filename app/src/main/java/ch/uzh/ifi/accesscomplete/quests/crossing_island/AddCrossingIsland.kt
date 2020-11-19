package ch.uzh.ifi.accesscomplete.quests.crossing_island

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.elementfilter.toElementFilterExpression
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.ktx.toYesNo
import ch.uzh.ifi.accesscomplete.quests.YesNoQuestAnswerFragment

class AddCrossingIsland : OsmElementQuestType<Boolean> {

    private val crossingFilter by lazy { """
        nodes with
          highway = crossing
          and crossing
          and crossing != island
          and !crossing:island
    """.toElementFilterExpression()}

    private val excludedWaysFilter by lazy { """
        ways with
          highway and access ~ private|no
          or highway and oneway and oneway != no
          or highway ~ path|footway|cycleway|pedestrian
    """.toElementFilterExpression()}

    override val commitMessage = "Add whether pedestrian crossing has an island"
    override val wikiLink = "Key:crossing:island"
    override val icon = R.drawable.ic_quest_pedestrian_crossing_island

    override fun getTitle(tags: Map<String, String>) = R.string.quest_pedestrian_crossing_island

    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> {
        val excludedWayNodeIds = mutableSetOf<Long>()
        mapData.ways
            .filter { excludedWaysFilter.matches(it) }
            .flatMapTo(excludedWayNodeIds) { it.nodeIds }

        return mapData.nodes
            .filter { crossingFilter.matches(it) && it.id !in excludedWayNodeIds }
    }

    override fun isApplicableTo(element: Element): Boolean? = null

    override fun createForm() = YesNoQuestAnswerFragment()

    override fun applyAnswerTo(answer: Boolean, changes: StringMapChangesBuilder) {
        changes.add("crossing:island", answer.toYesNo())
    }
}
