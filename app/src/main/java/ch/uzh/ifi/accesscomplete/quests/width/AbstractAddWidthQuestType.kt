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

package ch.uzh.ifi.accesscomplete.quests.width

import ch.uzh.ifi.accesscomplete.data.meta.updateWithCheckDate
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.AbstractPedestrianAccessibleWayFilterQuestType

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
