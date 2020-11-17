package de.westnordost.streetcomplete.quests.width

import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.AbstractPedestrianAccessibleWayFilterQuestType

abstract class AbstractAddWidthQuestType : AbstractPedestrianAccessibleWayFilterQuestType<AbstractWidthAnswer>() {

    override val commitMessage = "Add width info"
    override val wikiLink = "Key:width"
    override val isSplitWayEnabled = false

    override fun getOsmKey(): String = "width"

    override fun createForm(): AddWidthForm = AddWidthForm()

    override fun applyAnswerTo(answer: AbstractWidthAnswer, changes: StringMapChangesBuilder) {
        when (answer) {
            is SidewalkMappedSeparatelyAnswer -> {
                changes.updateWithCheckDate("sidewalk", answer.value)
                changes.deleteIfExists("source:sidewalk")
            }
            is SimpleWidthAnswer -> {
                changes.updateWithCheckDate(getOsmKey(), answer.value)
                changes.deleteIfExists("source:${getOsmKey()}")
            }
            is SidewalkWidthAnswer -> {
                if (answer.leftSidewalkAnswer != null) {
                    changes.updateWithCheckDate(getSidewalkLeftOsmKey(), answer.leftSidewalkAnswer!!.value)
                    changes.deleteIfExists("source:${getSidewalkLeftOsmKey()}")
                }
                if (answer.rightSidewalkAnswer != null) {
                    changes.updateWithCheckDate(getSidewalkRightOsmKey(), answer.rightSidewalkAnswer!!.value)
                    changes.deleteIfExists("source:${getSidewalkRightOsmKey()}")
                }
                changes.deleteIfExists(getSidewalkBothOsmKey())
                changes.deleteIfExists("source:${getSidewalkBothOsmKey()}")
            }
        }
    }
}
