package ch.uzh.ifi.accesscomplete.quests.wheelchair_access

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder

class AddWheelchairAccessOutside : OsmFilterQuestType<String>() {

    override val elementFilter = """
        nodes, ways, relations with leisure = dog_park
         and (!wheelchair or wheelchair older today -8 years)
    """
    override val commitMessage = "Add wheelchair access to outside places"
    override val wikiLink = "Key:wheelchair"
    override val icon = R.drawable.ic_quest_wheelchair_outside

    override fun getTitle(tags: Map<String, String>) = R.string.quest_wheelchairAccess_outside_title

    override fun createForm() = AddWheelchairAccessOutsideForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("wheelchair", answer)
    }
}
