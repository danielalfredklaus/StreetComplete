package de.westnordost.streetcomplete.quests.incline;

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmFilterQuestType

class AddPathIncline : OsmFilterQuestType<String>() {

    override val elementFilter = """
        ways with (
            highway = footway
            or (highway ~ path|cycleway|bridleway and foot != no)
        )
        and footway != crossing
        and access !~ private|no
        and (!conveying or conveying = no)
        and (!indoor or indoor = no)
        and (!area or area = no)
        and !incline
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
