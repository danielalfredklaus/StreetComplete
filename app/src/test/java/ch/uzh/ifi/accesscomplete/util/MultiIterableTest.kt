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

package ch.uzh.ifi.accesscomplete.util

import org.junit.Test

import org.junit.Assert.*

class MultiIterableTest {
    @Test fun `empty list`() {
        val itb = MultiIterable<String>()
        itb.add(emptyList())
        assertEquals("", itb.joinToString(" "))
    }

    @Test fun `one list`() {
        val itb = MultiIterable<String>()
        itb.add(listOf("a", "b", "c"))
        assertEquals("a b c", itb.joinToString(" "))
    }

    @Test fun `list allows nulls`() {
        val itb = MultiIterable<String?>()
        itb.add(listOf("a", null, "c"))
        assertEquals("a null c", itb.joinToString(" "))
    }

    @Test fun `multiple lists`() {
        val itb = MultiIterable<String>()
        itb.add(listOf("a", "b"))
        itb.add(listOf("c", "d"))
        assertEquals("a b c d", itb.joinToString(" "))
    }
}
