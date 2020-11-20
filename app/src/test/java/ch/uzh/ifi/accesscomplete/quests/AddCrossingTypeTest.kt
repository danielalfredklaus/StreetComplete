/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
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

package ch.uzh.ifi.accesscomplete.quests

import ch.uzh.ifi.accesscomplete.data.meta.toCheckDateString
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryAdd
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryModify
import ch.uzh.ifi.accesscomplete.quests.crossing_type.AddCrossingType
import org.junit.Test
import java.util.*

class AddCrossingTypeTest {

    private val questType = AddCrossingType()

    @Test fun `apply normal answer`() {
        questType.verifyAnswer(
            "bla",
            StringMapEntryAdd("crossing", "bla")
        )
    }

    @Test fun `apply answer for crossing = island`() {
        questType.verifyAnswer(
            mapOf("crossing" to "island"),
            "blub",
            StringMapEntryModify("crossing", "island", "blub"),
            StringMapEntryAdd("crossing:island", "yes")
        )
    }

    @Test fun `apply answer for crossing = island and crossing_island set`() {
        questType.verifyAnswer(
            mapOf("crossing" to "island", "crossing:island" to "something"),
            "blub",
            StringMapEntryModify("crossing", "island", "blub"),
            StringMapEntryModify("crossing:island", "something", "yes")
        )
    }

    @Test fun `apply marked answer does not change the type of marked value`() {
        questType.verifyAnswer(
            mapOf("crossing" to "zebra"),
            "uncontrolled",
            StringMapEntryAdd("check_date:crossing", Date().toCheckDateString())
        )

        questType.verifyAnswer(
            mapOf("crossing" to "marked"),
            "uncontrolled",
            StringMapEntryAdd("check_date:crossing", Date().toCheckDateString())
        )

        questType.verifyAnswer(
            mapOf("crossing" to "uncontrolled"),
            "uncontrolled",
            StringMapEntryAdd("check_date:crossing", Date().toCheckDateString())
        )

        questType.verifyAnswer(
            mapOf("crossing" to "unmarked"),
            "unmarked",
            StringMapEntryAdd("check_date:crossing", Date().toCheckDateString())
        )
    }
}
