package de.westnordost.accesscomplete.quests.smoothness

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.accesscomplete.data.elementfilter.toElementFilterExpression

class AddPedestrianAccessibleStreetSmoothness : AbstractAddSmoothnessQuestType() {

    private val baseExpression by lazy {
        """
            ways with highway ~ ${STREET_WITH_VALUABLE_SMOOTHNESS_INFO.joinToString("|")}
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_smoothness_street

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        val isSquare = tags["area"] == "yes"
        val hasSidewalk = hasSidewalk(tags)

        return if (hasName) {
            when {
                isSquare -> R.string.quest_smoothness_area_name_title
                hasSidewalk -> R.string.quest_smoothness_street_name_sidewalk_title
                else -> R.string.quest_smoothness_street_name_title
            }
        } else {
            when {
                isSquare -> R.string.quest_smoothness_area_title
                hasSidewalk -> R.string.quest_smoothness_street_sidewalk_title
                else -> R.string.quest_smoothness_street_title
            }
        }
    }

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = true

    companion object {
        private val STREET_WITH_VALUABLE_SMOOTHNESS_INFO = arrayOf(
            "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
            "unclassified", "residential", "living_street", "pedestrian", "track", "road"

            // These roads typically do not have any sidewalks (or they are mapped separately):
            // "trunk", "trunk_link", "motorway", "motorway_link",

            // This is too much, and the information value is very low:
            // "service"
        )
    }
}
