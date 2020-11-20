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

import org.junit.Assert.assertEquals

class FlattenIterableTest {
    @Test fun `empty list`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(emptyList<String>())
        assertEquals("", itb.joinToString(" "))
    }

    @Test fun `already flat list`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(listOf("a", "b", "c"))
        assertEquals("a b c", itb.joinToString(" "))
    }

    @Test fun `list allows nulls`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(listOf("a", null, "c"))
        assertEquals("a null c", itb.joinToString(" "))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `list with different types fails`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(listOf("a", 4))
        itb.joinToString(" ")
    }

    @Test fun `nested list`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(listOf("a", listOf("b", "c"), "d"))
        assertEquals("a b c d", itb.joinToString(" "))
    }

    @Test fun `deeper nested list`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(listOf("a", listOf("b", listOf("c", "d")), "e"))
        assertEquals("a b c d e", itb.joinToString(" "))
    }

    @Test fun `multiple lists`() {
        val itb = FlattenIterable(String::class.java)
        itb.add(listOf("a", "b", listOf("c", "d")))
        itb.add(listOf("e", "f"))
        assertEquals("a b c d e f", itb.joinToString(" "))
    }
}
