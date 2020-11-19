package de.westnordost.accesscomplete.quests.width

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.elementfilter.ElementFilterExpression
import de.westnordost.accesscomplete.data.elementfilter.toElementFilterExpression

class AddPathWidth : AbstractAddWidthQuestType() {

    private val baseExpression by lazy {
        """
            ways with (
              highway = footway
              or (highway ~ path|cycleway|bridleway and foot != no)
            )
            and footway != crossing
            and !level
            and segregated != yes
            and (!conveying or conveying = no)
            and (!indoor or indoor = no)
            and (!area or area = no)
        """.toElementFilterExpression()
    }

    override val icon = R.drawable.ic_quest_width_path

    override fun getTitle(tags: Map<String, String>) = R.string.quest_width_path_title

    override fun getBaseFilterExpression(): ElementFilterExpression = baseExpression

    // The ways provided in the baseExpression should not have sidewalk tags.
    override fun supportTaggingBySidewalkSide(): Boolean = false
}
