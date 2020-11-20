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

import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.*

class StringMapChangesTest {

    @Test fun empty() {
        val changes = StringMapChanges(emptyList())
        assertEquals("", changes.toString())
        assertTrue(changes.changes.isEmpty())

        // executable without error:
        val someMap = mutableMapOf("a" to "b")
        changes.applyTo(someMap)

        assertFalse(changes.hasConflictsTo(someMap))
    }

    @Test fun one() {
        val change: StringMapEntryChange = mock()
        on(change.toString()).thenReturn("x")

        val changes = StringMapChanges(listOf(change))
        val someMap = mutableMapOf("a" to "b")

        assertEquals("x", changes.toString())

        changes.applyTo(someMap)
        verify(change).applyTo(someMap)

        changes.hasConflictsTo(someMap)
        verify(change, atLeastOnce()).conflictsWith(someMap)
    }

    @Test fun two() {
        val change1: StringMapEntryChange = mock()
        on(change1.toString()).thenReturn("a")
        val change2: StringMapEntryChange = mock()
        on(change2.toString()).thenReturn("b")

        val changes = StringMapChanges(listOf(change1, change2))
        val someMap = mutableMapOf("a" to "b")

        assertEquals("a, b", changes.toString())

        changes.applyTo(someMap)
        verify(change1).applyTo(someMap)
        verify(change2).applyTo(someMap)

        changes.hasConflictsTo(someMap)
        verify(change1, atLeastOnce()).conflictsWith(someMap)
        verify(change2, atLeastOnce()).conflictsWith(someMap)
    }

    @Test(expected = IllegalStateException::class)
    fun `applying with conflict fails`() {
        val someMap = mutableMapOf<String, String>()

        val conflict: StringMapEntryChange = mock()
        on(conflict.conflictsWith(someMap)).thenReturn(true)

        val changes = StringMapChanges(listOf(conflict))

        changes.applyTo(someMap)
    }

    @Test fun getConflicts() {
        val someMap = emptyMap<String, String>()

        val conflict: StringMapEntryChange = mock()
        on(conflict.conflictsWith(someMap)).thenReturn(true)

        val changes = StringMapChanges(listOf(mock(), mock(), conflict, mock(), conflict))

        changes.getConflictsTo(someMap)

        val it = changes.getConflictsTo(someMap).iterator()

        assertSame(conflict, it.next())
        assertSame(conflict, it.next())
    }
}
