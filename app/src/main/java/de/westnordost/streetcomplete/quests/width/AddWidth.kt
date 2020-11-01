package de.westnordost.streetcomplete.quests.width

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmFilterQuestType

class AddWidth : OsmFilterQuestType<String>() {

    override val elementFilter = """
        ways with highway ~ path|footway
        and access !~ private|no
        and (!conveying or conveying = no) and (!indoor or indoor = no)
        and (!width or width = no)
    """

    override val commitMessage = "Add width"
    override val wikiLink = "Key:width"
    override val icon = R.drawable.ic_quest_street_width
    override val isSplitWayEnabled = false

    override fun getTitle(tags: Map<String, String>) = when { // TODO sst: change title
        tags["highway"] == "bridleway" -> R.string.quest_pathSurface_title_bridleway
        tags["highway"] == "steps"     -> R.string.quest_pathSurface_title_steps
        else                           -> R.string.quest_pathSurface_title
        // rest is rather similar, can be called simply "path"
    }

    override fun createForm(): AddWidthForm = AddWidthForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("width", answer)
    }
}
