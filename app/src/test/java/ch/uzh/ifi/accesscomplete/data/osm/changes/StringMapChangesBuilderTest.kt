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

class StringMapChangesBuilderTest {

    @Test fun delete() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.delete("exists")
        val change = builder.create().changes.single() as StringMapEntryDelete
        assertEquals("exists", change.key)
        assertEquals("like this", change.valueBefore)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `delete non-existing fails`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.delete("does not exist")
    }

    @Test fun `deleteIfExists non-existing does not fail`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.deleteIfExists("does not exist")
    }

    @Test fun `deleteIfPreviously non-existing does not fail`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.deleteIfPreviously("does not exist", "a")
        assertTrue(builder.create().isEmpty())
    }

    @Test fun `deleteIfPreviously key with different value does not fail`() {
        val builder = StringMapChangesBuilder(mapOf("a" to "b"))
        builder.deleteIfPreviously("a", "c")
        assertTrue(builder.create().isEmpty())
    }

    @Test fun `deleteIfPreviously key with correct value deletes it`() {
        val builder = StringMapChangesBuilder(mapOf("a" to "b"))
        builder.deleteIfPreviously("a", "b")
        assertEquals(
            StringMapEntryDelete("a", "b"),
            builder.create().changes.single()
        )
    }

    @Test fun add() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.add("does not exist", "but now")
        val change = builder.create().changes.single() as StringMapEntryAdd
        assertEquals("does not exist", change.key)
        assertEquals("but now", change.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `add already existing fails`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.add("exists", "like that")
    }

    @Test fun modify() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.modify("exists", "like that")
        val change = builder.create().changes.single() as StringMapEntryModify
        assertEquals("exists", change.key)
        assertEquals("like this", change.valueBefore)
        assertEquals("like that", change.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `modify non-existing does fail`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.modify("does not exist", "bla")
    }

    @Test fun `modifyIfExists non-existing does not fail`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.modifyIfExists("does not exist", "bla")
    }

    @Test(expected = IllegalStateException::class)
    fun `duplicate change on same key fails`() {
        val builder = StringMapChangesBuilder(mapOf("exists" to "like this"))
        builder.modify("exists", "like that")
        builder.delete("exists")
    }
}
