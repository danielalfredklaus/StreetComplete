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

package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import ch.uzh.ifi.accesscomplete.data.elementfilter.dateDaysAgo
import ch.uzh.ifi.accesscomplete.data.elementfilter.matches
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDateString
import org.junit.Assert.*
import org.junit.Test

class TagNewerThanTest {
    private val oldDate = dateDaysAgo(101f)
    private val newDate = dateDaysAgo(99f)

    val c = TagNewerThan("opening_hours", RelativeDate(-100f))

    @Test fun `does not match old element with tag`() {
        assertFalse(c.matches(mapOf("opening_hours" to "tag"), oldDate))
    }

    @Test fun `matches new element with tag`() {
        assertTrue(c.matches(mapOf("opening_hours" to "tag"), newDate))
    }

    @Test fun `matches old element with tag and new check_date`() {
        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "opening_hours:check_date" to newDate.toCheckDateString()
        ), oldDate))

        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "check_date:opening_hours" to newDate.toCheckDateString()
        ), oldDate))
    }

    @Test fun `matches old element with tag and new lastcheck`() {
        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "opening_hours:lastcheck" to newDate.toCheckDateString()
        ), oldDate))

        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "lastcheck:opening_hours" to newDate.toCheckDateString()
        ), oldDate))
    }

    @Test fun `matches old element with tag and new last_checked`() {
        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "opening_hours:last_checked" to newDate.toCheckDateString()
        ), oldDate))

        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "last_checked:opening_hours" to newDate.toCheckDateString()
        ), oldDate))
    }

    @Test fun `matches old element with tag and different check date tags of which only one is new`() {
        assertTrue(c.matches(mapOf(
            "opening_hours" to "tag",
            "opening_hours:last_checked" to oldDate.toCheckDateString(),
            "opening_hours:lastcheck" to newDate.toCheckDateString(),
            "opening_hours:check_date" to oldDate.toCheckDateString(),
            "last_checked:opening_hours" to oldDate.toCheckDateString(),
            "lastcheck:opening_hours" to oldDate.toCheckDateString(),
            "check_date:opening_hours" to oldDate.toCheckDateString()
        ), oldDate))
    }

    @Test fun `to string`() {
        val date = dateDaysAgo(100f).toCheckDateString()
        assertEquals(
            "(if: date(timestamp()) > date('$date') || " +
            "date(t['opening_hours:check_date']) > date('$date') || " +
            "date(t['check_date:opening_hours']) > date('$date') || " +
            "date(t['opening_hours:lastcheck']) > date('$date') || " +
            "date(t['lastcheck:opening_hours']) > date('$date') || " +
            "date(t['opening_hours:last_checked']) > date('$date') || " +
            "date(t['last_checked:opening_hours']) > date('$date'))",
            c.toOverpassQLString()
        )
    }
}
