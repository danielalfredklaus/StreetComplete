package de.westnordost.streetcomplete.quests.smoothness

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.AbstractPedestrianAccessibleWayFilterQuestType

abstract class AbstractAddSmoothnessQuestType : AbstractPedestrianAccessibleWayFilterQuestType<AbstractSmoothnessAnswer>() {

    override val commitMessage = "Add smoothness info"
    override val wikiLink = "Key:smoothness"
    override val isSplitWayEnabled = true

    override fun getOsmKey(): String = "smoothness"

    override fun createForm(): AddSmoothnessForm = AddSmoothnessForm()

    override fun applyAnswerTo(answer: AbstractSmoothnessAnswer, changes: StringMapChangesBuilder) {
        when (answer) {
            is SimpleSmoothnessAnswer -> {
                changes.updateWithCheckDate(getOsmKey(), answer.value)
                changes.deleteIfExists("source:${getOsmKey()}")
            }
            is SidewalkSmoothnessAnswer -> {
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
