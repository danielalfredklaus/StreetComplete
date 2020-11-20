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

package ch.uzh.ifi.accesscomplete.data.osm.changes

import org.junit.Test

import org.junit.Assert.*

class StringMapEntryDeleteTest {

    @Test fun delete() {
        val c = StringMapEntryDelete("a", "b")
        val m = mutableMapOf("a" to "c")

        assertEquals("DELETE \"a\"=\"b\"", c.toString())

        assertTrue(c.conflictsWith(m))
        m["a"] = "b"
        assertFalse(c.conflictsWith(m))

        c.applyTo(m)
        assertFalse(m.containsKey("a"))
        assertTrue(c.conflictsWith(m))
    }

    @Test fun reverse() {
        val m = mutableMapOf("a" to "b")

        val delete = StringMapEntryDelete("a", "b")
        val reverseDelete = delete.reversed()

        delete.applyTo(m)
        reverseDelete.applyTo(m)

        assertEquals(1, m.size)
        assertEquals("b", m["a"])
    }
}
