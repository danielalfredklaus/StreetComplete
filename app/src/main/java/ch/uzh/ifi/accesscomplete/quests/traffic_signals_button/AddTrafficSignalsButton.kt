package ch.uzh.ifi.accesscomplete.quests.traffic_signals_button

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.ktx.toYesNo
import ch.uzh.ifi.accesscomplete.quests.YesNoQuestAnswerFragment

class AddTrafficSignalsButton : OsmFilterQuestType<Boolean>() {

    override val elementFilter = """
        nodes with crossing = traffic_signals and highway ~ crossing|traffic_signals
        and !button_operated
        """
    override val commitMessage = "Add whether traffic signals have a button for pedestrians"
    override val wikiLink = "Tag:highway=traffic_signals"
    override val icon = R.drawable.ic_quest_traffic_lights

    override fun getTitle(tags: Map<String, String>) = R.string.quest_traffic_signals_button_title

    override fun createForm() = YesNoQuestAnswerFragment()

    override fun applyAnswerTo(answer: Boolean, changes: StringMapChangesBuilder) {
        changes.add("button_operated", answer.toYesNo())
    }
}
