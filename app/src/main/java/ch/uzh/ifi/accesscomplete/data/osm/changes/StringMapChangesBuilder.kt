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

class StringMapChangesBuilder(private val source: Map<String, String>) {
    private val changes: MutableMap<String, StringMapEntryChange> = mutableMapOf()

    fun delete(key: String) {
        val valueBefore = requireNotNull(source[key]) { "The key '$key' does not exist in the map." }
        val change = StringMapEntryDelete(key, valueBefore)
        if (changes[key] == change) return
        checkDuplicate(key)
        changes[key] = change
    }

    fun deleteIfExists(key: String) {
        if (source[key] != null) {
            delete(key)
        }
    }

    fun deleteIfPreviously(key: String, valueBefore: String) {
        if (source[key] == valueBefore) {
            delete(key)
        }
    }

    fun add(key: String, value: String) {
        require(!source.containsKey(key)) { "The key '$key' already exists in the map." }
        val change = StringMapEntryAdd(key, value)
        if (changes[key] == change) return
        checkDuplicate(key)
        changes[key] = change
    }

    fun modify(key: String, value: String) {
        val valueBefore = requireNotNull(source[key]) {"The key '$key' does not exist in the map." }
        val change = StringMapEntryModify(key, valueBefore, value)
        if (changes[key] == change) return
        checkDuplicate(key)
        changes[key] = change
    }

    fun addOrModify(key: String, value: String) {
        val valueBefore = source[key]
        if (valueBefore == null) {
            add(key, value)
        } else {
            modify(key, value)
        }
    }

    fun modifyIfExists(key: String, value: String) {
        if (source[key] != null) {
            modify(key, value)
        }
    }

    fun getPreviousValue(key: String): String? {
        return source[key]
    }

    fun getPreviousEntries(): Map<String, String> {
        return source.toMap()
    }

    fun getChanges(): List<StringMapEntryChange> = changes.values.toList()

    private fun checkDuplicate(key: String) {
        check(!changes.containsKey(key)) { "The key '$key' is already being modified." }
    }

    fun create() = StringMapChanges(ArrayList(changes.values))
}
