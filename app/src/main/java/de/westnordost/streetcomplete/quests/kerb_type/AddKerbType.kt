package de.westnordost.streetcomplete.quests.kerb_type

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Node
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmElementQuestType

/**
 * A quest that asks about the kerb types of applicable nodes of footway crossings. The crossings
 * must be tagged according to the
 * [specification of the "footway=crossing" tag](https://wiki.openstreetmap.org/wiki/Tag:footway%3Dcrossing).
 * Nodes that are simply tagged with "highway=crossing" on a streets without a separately mapped
 * sidewalk will not be involved, because routing algorithms do not evaluate kerbs in such cases
 * ([see related discussion for the OSM iD editor](https://github.com/openstreetmap/iD/issues/6078))
 */
class AddKerbType : OsmElementQuestType<String> {

    override val commitMessage = "Add kerb to crossing"
    override val wikiLink = "Key:kerb"
    override val icon = R.drawable.ic_quest_kerb
    override val isSplitWayEnabled = false

    // Filters eligible footway crossings where kerb related tags are not already present on the way
    // itself. Although this would not be in accordance to the specification, it is something that
    // sometimes occur and we do not want to duplicate tags on nodes in such a situation.
    private val footwayCrossingWayFilter by lazy { """
        ways with highway = footway
        and footway = crossing
        and access !~ private|no
        and (barrier != kerb or (barrier = kerb and (!kerb or !kerb:left or !kerb:right)))
    """.toElementFilterExpression() }

    // It is unlikely that lowered or flushed kerbs will become raised again, hence only raised and
    // rolled kerbs are applicable for a resurvey.
    private val filterNotAlreadyTaggedOrOlderThanCheckDate by lazy { """
        nodes with !kerb
        or kerb ~ yes|unknown
        or (kerb ~ raised|rolled and older today -8 years)
    """.toElementFilterExpression() }

    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> {
        val footwayCrossingWays = mapData.ways.filter { footwayCrossingWayFilter.matches(it) }
        val applicableNodes = mutableSetOf<Node>()
        footwayCrossingWays.forEach { way ->
            if (way.nodeIds.size < 3) {
                // This crossing way is not tagged according to the specification --> omit
                return@forEach
            }

            val nodeIdIterator = way.nodeIds.iterator()
            var previousNode: Node? = null
            while (nodeIdIterator.hasNext()) {
                val currentNode = mapData.getNode(nodeIdIterator.next())

                if (currentNode != null) {
                    if (isCrossing(currentNode.tags)) {
                        // Kerbs on crossing islands should be tagged as well
                        if (isCrossingIsland(currentNode.tags)) {
                            applicableNodes.add(currentNode)
                        }

                        // If there is a previous node it should be added as long as it is not a
                        // crossing node itself. If the previous node is a crossing node that is
                        // also a crossing island then it was already added.
                        if (previousNode != null && !isCrossing(previousNode.tags)) {
                            applicableNodes.add(previousNode)
                        }
                    } else {
                        // After a crossing node, the next node should be added if it is not a
                        // crossing node itself.
                        if (previousNode != null && isCrossing(previousNode.tags)) {
                            applicableNodes.add(currentNode)
                        }
                    }
                }
                previousNode = currentNode
            }
        }
        applicableNodes.filter { filterNotAlreadyTaggedOrOlderThanCheckDate.matches(it) }

        // TODO sst: remove after usability tests
        applicableNodes.addAll(addNodesForTesting(mapData))
        return applicableNodes
    }

    private fun addNodesForTesting(mapData: MapDataWithGeometry): List<Node> {
        return mapData.nodes.filter {  it.id == 3513203095L ||
                it.id == 1185766268L ||
                it.id == 3970049719L ||
                it.id == 3513203095L }
    }

    private fun isCrossing(tags: Map<String, String>): Boolean {
        return "crossing" == tags["highway"] || !tags["crossing"].isNullOrEmpty()
    }

    private fun isCrossingIsland(tags: Map<String, String>): Boolean {
        return "island" == tags["traffic_calming"] || "yes" == tags["crossing:island"]
    }

    override fun isApplicableTo(element: Element): Boolean? = null

    override fun getTitle(tags: Map<String, String>) = when {
        isCrossingIsland(tags) -> R.string.quest_kerb_type_traffic_island_title
        else -> R.string.quest_kerb_type_title
    }

    override fun createForm(): AddKerbTypeForm = AddKerbTypeForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        if (answer == NO_KERB_VALUE) {
            if (changes.getPreviousValue("barrier") == "kerb") {
                changes.deleteIfExists("barrier")
                changes.deleteIfExists("source:barrier")
            }
        } else {
            changes.updateWithCheckDate("barrier", "kerb")
            changes.deleteIfExists("source:barrier")
        }

        changes.updateWithCheckDate("kerb", answer)
        changes.deleteIfExists("kerb:left")
        changes.deleteIfExists("kerb:right")

        changes.deleteIfExists("source:kerb")
        changes.deleteIfExists("source:kerb:left")
        changes.deleteIfExists("source:kerb:right")
    }

    companion object {
        const val NO_KERB_VALUE = "no"
    }
}
