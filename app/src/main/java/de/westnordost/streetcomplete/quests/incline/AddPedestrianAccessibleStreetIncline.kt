package de.westnordost.streetcomplete.quests.incline;

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmFilterQuestType

class AddPedestrianAccessibleStreetIncline : OsmFilterQuestType<String>() {

    override val elementFilter = """
        ways with (
            (highway ~ ${STREETS_WITH_VALUABLE_INCLINE_INFO.joinToString("|")} and sidewalk ~ left|right|both)
            or (highway ~ ${PEDESTRIAN_ACCESSIBLE_STREETS_WITH_VALUABLE_INCLINE_INFO.joinToString("|")}))
        and (access !~ private|no or (foot and foot !~ private|no))
        and (!area or area = no)
        and !incline
    """

    override val commitMessage = "Add incline info"
    override val wikiLink = "Key:incline"
    override val icon = R.drawable.ic_quest_incline_street
    override val indicateDirection = true
    override val isSplitWayEnabled = false

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        return if (hasName)
                R.string.quest_incline_street_name_title
            else
                R.string.quest_incline_street_title
    }

    override fun createForm(): AddInclineForm = AddInclineForm()

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate("incline", answer)
    }

    companion object {
        private val PEDESTRIAN_ACCESSIBLE_STREETS_WITH_VALUABLE_INCLINE_INFO = arrayOf(
            "living_street", "pedestrian"
        )

        private val STREETS_WITH_VALUABLE_INCLINE_INFO = arrayOf(
            "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
            "unclassified", "residential", "track", "road"

            // These roads typically do not have any sidewalks (or they are mapped separately):
            // "trunk", "trunk_link", "motorway", "motorway_link",

            // This is too much, and the information value is very low:
            // "service"
        )

    }
}
