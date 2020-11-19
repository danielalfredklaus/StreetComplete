package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestAnswerFragment
import de.westnordost.osmapi.map.data.Element

open class TestQuestType : OsmElementQuestType<String> {

    override fun getTitle(tags: Map<String, String>) = 0
    override fun isApplicableTo(element: Element):Boolean? = null
    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {}
    override val icon = 0
    override fun createForm(): AbstractQuestAnswerFragment<String> = object : AbstractQuestAnswerFragment<String>() {}
    override val commitMessage = ""
    override fun getApplicableElements(mapData: MapDataWithGeometry) = emptyList<Element>()
}
