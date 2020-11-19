package de.westnordost.accesscomplete.quests.surface

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.accesscomplete.data.elementfilter.toElementFilterExpression

class AddCyclewayPartSurface : AbstractAddSurfaceQuestType() {

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

    override val icon = R.drawable.ic_quest_surface_cycleway

    override fun getTitle(tags: Map<String, String>) = R.string.quest_cyclewayPartSurface_title

    override fun getOsmKey(): String = "cycleway:surface"

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = false
}
