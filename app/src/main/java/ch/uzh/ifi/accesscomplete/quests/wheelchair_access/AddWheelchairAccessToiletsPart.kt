package ch.uzh.ifi.accesscomplete.quests.wheelchair_access

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder

class AddWheelchairAccessToiletsPart : OsmFilterQuestType<String>() {

    override val elementFilter = """
        nodes, ways, relations with name and toilets = yes
         and (
           !toilets:wheelchair
           or toilets:wheelchair != yes and toilets:wheelchair older today -4 years
           or toilets:wheelchair older today -8 years
         )
    """
    override val commitMessage = "Add wheelchair access to toilets"
    override val wikiLink = "Key:toilets:wheelchair"
    override val icon = R.drawable.ic_quest_toilets_wheelchair
    override val defaultDisabledMessage = R.string.default_disabled_msg_go_inside

    override fun getTitle(tags: Map<String, String>) = R.string.quest_wheelchairAccess_toiletsPart_title

    override fun createForm() = AddWheelchairAccessToiletsForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("toilets:wheelchair", answer)
    }
}
