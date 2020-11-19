package de.westnordost.accesscomplete.quests.width

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.accesscomplete.data.elementfilter.toElementFilterExpression

class AddPedestrianAccessibleStreetWidth : AbstractAddWidthQuestType() {

    private val baseExpression by lazy {
        """
            ways with highway ~ ${STREETS_WITH_VALUABLE_WIDTH_INFO.joinToString("|")}
            and (!area or area = no)
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_width_street

    override fun getTitle(tags: Map<String, String>): Int {
        val hasName = tags.containsKey("name")
        val hasSidewalk = hasSidewalk(tags)

        return if (hasName) {
            when {
                hasSidewalk -> R.string.quest_width_street_name_sidewalk_title
                else -> R.string.quest_width_street_name_title
            }
        } else {
            when {
                hasSidewalk -> R.string.quest_width_street_sidewalk_title
                else -> R.string.quest_width_street_title
            }
        }
    }

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = true

    companion object {
        private val STREETS_WITH_VALUABLE_WIDTH_INFO = arrayOf(
            "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
            "unclassified", "residential", "living_street", "track", "road"

            // These streets typically do not have any sidewalks (or they are mapped separately):
            // "trunk", "trunk_link", "motorway", "motorway_link"

            // Streets that are typically very wide and are not worth measuring:
            // "pedestrian"

            // This is too much, and the information value is very low:
            // "service"
        )
    }
}
