package ch.uzh.ifi.accesscomplete.quests.width

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementFilterExpression
import ch.uzh.ifi.accesscomplete.data.elementfilter.toElementFilterExpression

class AddCyclewayPartWidth : AbstractAddWidthQuestType() {

    private val baseExpression by lazy {
        """
            ways with (
              highway = cycleway
              or (highway ~ path|footway and bicycle != no)
              or (highway = bridleway and bicycle ~ designated|yes)
            )
            and segregated = yes
            and (!area or area = no)
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_width_cycleway

    override fun getTitle(tags: Map<String, String>) = R.string.quest_width_cycleway_part_title

    override fun getOsmKey(): String = "cycleway:width"

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = false
}
