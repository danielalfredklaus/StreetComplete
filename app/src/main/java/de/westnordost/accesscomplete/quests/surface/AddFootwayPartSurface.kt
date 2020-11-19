package de.westnordost.accesscomplete.quests.surface

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.accesscomplete.data.elementfilter.toElementFilterExpression

class AddFootwayPartSurface : AbstractAddSurfaceQuestType() {

    private val baseExpression by lazy {
        """
            ways with (
              highway = footway
              or (highway ~ path|cycleway|bridleway and foot != no)
            )
            and segregated = yes
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_surface_footway

    override fun getTitle(tags: Map<String, String>) = R.string.quest_footwayPartSurface_title

    override fun getOsmKey(): String = "footway:surface"

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    override fun supportTaggingBySidewalkSide(): Boolean = false
}
