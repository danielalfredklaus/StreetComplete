package de.westnordost.streetcomplete.quests.smoothness

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression

class AddCyclewayPartSmoothness : AbstractAddSmoothnessQuestType() {

    private val baseExpression by lazy {
        """
            ways with (
              highway = cycleway
              or (highway ~ path|footway and bicycle != no)
              or (highway = bridleway and bicycle ~ designated|yes)
            )
            and segregated = yes
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_smoothness_cycleway

    override fun getTitle(tags: Map<String, String>) = R.string.quest_smoothness_cycleway_part_title

    override fun getOsmKey(): String = "cycleway:smoothness"

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = false
}
