package ch.uzh.ifi.accesscomplete.quests.incline;

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType

class AddPathIncline : OsmFilterQuestType<String>() {

    override val elementFilter = """
        ways with (
            highway = footway
            or (highway ~ path|cycleway|bridleway and foot != no)
        )
        and access !~ private|no
        and foot !~ private|no|use_sidepath
        and footway != crossing
        and !level
        and (!conveying or conveying = no)
        and (!indoor or indoor = no)
        and (!area or area = no)
        and (!incline or incline older today -8 years)
    """

    override val commitMessage = "Add incline info"
    override val wikiLink = "Key:incline"
    override val icon = R.drawable.ic_quest_incline_path
    override val indicateDirection = true
    override val isSplitWayEnabled = false

    override fun getTitle(tags: Map<String, String>) = R.string.quest_incline_path_title

    override fun createForm(): AddInclineForm = AddInclineForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("incline", answer)
    }
}
