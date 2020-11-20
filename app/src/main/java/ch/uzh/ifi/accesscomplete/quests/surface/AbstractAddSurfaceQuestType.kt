/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.quests.surface

import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.AbstractPedestrianAccessibleWayFilterQuestType

abstract class AbstractAddSurfaceQuestType : AbstractPedestrianAccessibleWayFilterQuestType<AbstractSurfaceAnswer>() {

    override val commitMessage = "Add surface info"
    override val wikiLink = "Key:surface"
    override val isSplitWayEnabled = true

    override fun getOsmKey(): String = "surface"

    override fun createForm(): AddSurfaceForm = AddSurfaceForm()

    override fun applyAnswerTo(answer: AbstractSurfaceAnswer, changes: StringMapChangesBuilder) {
        when (answer) {
            is SidewalkMappedSeparatelyAnswer -> {
                changes.updateWithCheckDate("sidewalk", answer.value)
                changes.deleteIfExists("source:sidewalk")
            }
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
