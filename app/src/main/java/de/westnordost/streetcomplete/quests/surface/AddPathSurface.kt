package de.westnordost.streetcomplete.quests.surface

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression

class AddPathSurface : AbstractAddSurfaceQuestType() {

    private val baseExpression by lazy {
        """
            ways with (
              highway = footway
              or (highway ~ path|cycleway|bridleway and foot != no)
            )
            and segregated != yes
            and footway != crossing
            and !level
            and access !~ private|no
            and (!conveying or conveying = no)
            and (!indoor or indoor = no)
            and (!area or area = no)
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_surface_path

    override fun getTitle(tags: Map<String, String>) = when {
        tags["area"] == "yes" ->        R.string.quest_streetSurface_square_title
        else -> R.string.quest_pathSurface_title
    }

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    // The ways provided in the baseExpression should not have sidewalk tags.
    override fun supportTaggingBySidewalkSide(): Boolean = false
}
