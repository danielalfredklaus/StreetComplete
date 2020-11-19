package ch.uzh.ifi.accesscomplete.quests.width

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementFilterExpression
import ch.uzh.ifi.accesscomplete.data.elementfilter.toElementFilterExpression

class AddFootwayPartWidth : AbstractAddWidthQuestType() {

    private val baseExpression by lazy {
        """
            ways with (
              highway = footway
              or (highway ~ path|cycleway|bridleway and foot != no)
            )
            and segregated = yes
            and (!area or area = no)
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_width_footway

    override fun getTitle(tags: Map<String, String>) = R.string.quest_width_footway_part_title

    override fun getOsmKey(): String = "footway:width"

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = false
}
