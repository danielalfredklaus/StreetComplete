package de.westnordost.streetcomplete.quests.smoothness

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.OsmFilterQuestType

class AddPathSmoothness : OsmFilterQuestType<AbstractSmoothnessAnswer>() {

    override val elementFilter = """
        ways with highway ~ path|footway
        and segregated != yes
        and access !~ private|no
        and (!conveying or conveying = no)
        and (!indoor or indoor = no)
        and (!smoothness or smoothness older today -8 years)
    """

    override val commitMessage = "Add path smoothness"
    override val wikiLink = "Key:smoothness"
    override val icon = R.drawable.ic_quest_smoothness
    override val isSplitWayEnabled = true

    override fun getTitle(tags: Map<String, String>) = when {
        tags["area"] == "yes" -> R.string.quest_streetSurface_square_title // TODO sst change
        else -> R.string.quest_path_smoothness_title
    }

    override fun createForm(): AddSmoothnessForm = AddSmoothnessForm()

    override fun applyAnswerTo(answer: AbstractSmoothnessAnswer, changes: StringMapChangesBuilder) {
        when(answer) {
            is SimpleSmoothnessAnswer -> {
                changes.updateWithCheckDate("smoothness", answer.value)
                changes.deleteIfExists("source:smoothness")
            }
            is SidewalkSmoothnessAnswer -> {
                if (answer.leftSidewalkValue != null) {
                    changes.updateWithCheckDate("sidewalk:left:smoothness", answer.leftSidewalkValue!!.value)
                    changes.deleteIfExists("source:sidewalk:left:smoothness")
                }
                if (answer.rightSidewalkValue != null) {
                    changes.updateWithCheckDate("sidewalk:right:smoothness", answer.rightSidewalkValue!!.value)
                    changes.deleteIfExists("source:sidewalk:right:smoothness")
                }
                changes.deleteIfExists("sidewalk:both:smoothness")
                changes.deleteIfExists("source:sidewalk:both:smoothness")
            }
        }
    }
}
