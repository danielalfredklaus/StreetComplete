package de.westnordost.streetcomplete.quests.kerb_type

import de.westnordost.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Node
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmElementQuestType

class AddKerbType : OsmElementQuestType<String> {

    private val footwayCrossingWayFilter by lazy { """
        ways with highway = footway
        and footway = crossing
        and (!barrier or barrier != kerb or (barrier = kerb and (!kerb or !kerb:left or !kerb:right)))
    """.toElementFilterExpression() }

    override val commitMessage = "Add kerb to crossing"
    override val wikiLink = "Key:kerb"
    override val icon = R.drawable.ic_quest_kerb
    override val isSplitWayEnabled = false

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
                        if (isCrossingIsland(currentNode.tags) && !kerbAlreadyTagged(currentNode.tags)) {
                            applicableNodes.add(currentNode)
                        }

                        // If there is a previous node that does not have the kerb tags,
                        // it should be added as long as it is not a crossing node itself.
                        // If the previous node is a crossing node that is also a crossing island
                        // then it was already added.
                        if (previousNode != null && !isCrossing(previousNode.tags) && !kerbAlreadyTagged(previousNode.tags)) {
                            applicableNodes.add(previousNode)
                        }
                    } else {
                        // After a crossing node, the next node should be added if it is not a
                        // crossing node itself and does not have the kerb tags already.
                        if (previousNode != null && isCrossing(previousNode.tags) && !kerbAlreadyTagged(previousNode.tags)) {
                            applicableNodes.add(currentNode)
                        }
                    }
                }
                previousNode = currentNode
            }
        }
        return applicableNodes
    }

    private fun isCrossing(tags: Map<String, String>): Boolean {
        return "crossing" == tags["highway"]
    }

    private fun isCrossingIsland(tags: Map<String, String>): Boolean {
        return "island" == tags["traffic_calming"] || "yes" == tags["crossing:island"]
    }

    private fun kerbAlreadyTagged(tags: Map<String, String>): Boolean {
        return "kerb" == tags["barrier"] && !tags["kerb"].isNullOrEmpty()
    }

    override fun isApplicableTo(element: Element): Boolean? = null

    override fun getTitle(tags: Map<String, String>) = when {
        isCrossingIsland(tags) -> R.string.quest_kerb_type_traffic_island_title
        else -> R.string.quest_kerb_type_title
    }

    override fun createForm(): AddKerbTypeForm = AddKerbTypeForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("barrier", "kerb")
        changes.updateWithCheckDate("kerb", answer)
        changes.deleteIfExists("kerb:left")
        changes.deleteIfExists("kerb:right")

        changes.deleteIfExists("source:barrier")
        changes.deleteIfExists("source:kerb")
        changes.deleteIfExists("source:kerb:left")
        changes.deleteIfExists("source:kerb:right")
    }
}
