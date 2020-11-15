package de.westnordost.streetcomplete.quests.smoothness

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression

class AddPathSmoothness : AbstractAddSmoothnessQuestType() {

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

    override val icon = R.drawable.ic_quest_smoothness_path

    override fun getTitle(tags: Map<String, String>) = R.string.quest_smoothness_path_title

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    // The ways provided in the baseExpression should not have sidewalk tags.
    override fun supportTaggingBySidewalkSide(): Boolean = false
}
