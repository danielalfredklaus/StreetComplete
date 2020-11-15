package de.westnordost.streetcomplete.quests.surface

import de.westnordost.streetcomplete.data.meta.updateWithCheckDate
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.osmquest.AbstractPedestrianAccessibleWayFilterQuestType

abstract class AbstractAddSurfaceQuestType : AbstractPedestrianAccessibleWayFilterQuestType<AbstractSurfaceAnswer>() {

    override val commitMessage = "Add surface info"
    override val wikiLink = "Key:surface"
    override val isSplitWayEnabled = true

    override fun getOsmKey(): String = "surface"

    override fun createForm(): AddSurfaceForm = AddSurfaceForm()

    override fun applyAnswerTo(answer: AbstractSurfaceAnswer, changes: StringMapChangesBuilder) {
        when (answer) {
            is SidewalkSurfaceAnswer -> {
                applySurfaceAnswer(getSidewalkLeftOsmKey(), answer.leftSidewalkAnswer, changes)
                applySurfaceAnswer(getSidewalkRightOsmKey(), answer.rightSidewalkAnswer, changes)
            }
            is SurfaceAnswer -> {
                applySurfaceAnswer(getOsmKey(), answer, changes)
            }
        }
    }

    private fun applySurfaceAnswer(surfaceTag: String, surfaceAnswer: SurfaceAnswer?, changes: StringMapChangesBuilder) {
        if (surfaceAnswer == null) {
            return
        }
        when (surfaceAnswer) {
            is SpecificSurfaceAnswer -> {
                changes.updateWithCheckDate(surfaceTag, surfaceAnswer.value)
                changes.deleteIfExists("$surfaceTag:note")
            }
            is GenericSurfaceAnswer -> {
                changes.updateWithCheckDate(surfaceTag, surfaceAnswer.value)
                changes.addOrModify("$surfaceTag:note", surfaceAnswer.note)
            }
        }
        changes.deleteIfExists("source:$surfaceTag")
    }
}
