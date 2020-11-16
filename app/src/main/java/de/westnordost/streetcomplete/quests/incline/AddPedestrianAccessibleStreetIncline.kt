package de.westnordost.streetcomplete.quests.incline;

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmFilterQuestType

class AddPedestrianAccessibleStreetIncline : OsmFilterQuestType<String>() {

    override val elementFilter = """
        ways with (
            (highway ~ ${ASSUMED_PEDESTRIAN_INACCESSIBLE_STREETS.joinToString("|")} and sidewalk ~ left|right|both)
            or (highway ~ ${ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS.joinToString("|")})
            or (highway ~ ${ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS_IF_NO_SIDEWALK.joinToString("|")} and sidewalk ~ no|none)
        )
        and sidewalk !~ separate|use_sidepath
        and access !~ private|no
        and foot !~ private|no|use_sidepath
        and (!area or area = no)
        and (!incline or incline older today -8 years)
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
        // For the following values of the highway tag, there needs to be no info about the
        // existence of a sidewalk in order to be applicable for a quest.
        private val ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS = arrayOf(
            "pedestrian", "living_street"
        )

        private val ASSUMED_PEDESTRIAN_ACCESSIBLE_STREETS_IF_NO_SIDEWALK = arrayOf(
            "residential", "road", "unclassified"
        )

        private val ASSUMED_PEDESTRIAN_INACCESSIBLE_STREETS = arrayOf(
            "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
            "unclassified", "residential", "track",

            // These roads are typically never used by pedestrians and do not have sidewalks
            // (or they are mapped separately):
            // "trunk", "trunk_link", "motorway", "motorway_link",

            // This is too much, and the information value is very low:
            // "service"
        )
    }
}
