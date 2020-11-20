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

import android.content.SharedPreferences
import androidx.core.content.edit

import java.util.LinkedList

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.view.image_select.DisplayItem
import ch.uzh.ifi.accesscomplete.view.image_select.GroupableDisplayItem
import kotlin.math.min

/** T must be a string or enum - something that distinctly converts toString. */
class LastPickedValuesStore<T> @Inject constructor(private val prefs: SharedPreferences) {

    fun add(key: String, newValues: Iterable<T>, max: Int = -1) {
        val values = get(key)
        for (value in newValues.map { it.toString() }) {
            values.remove(value)
            values.addFirst(value)
        }
        val lastValues = if (max != -1) values.subList(0, min(values.size, max)) else values
        prefs.edit {
            putString(getKey(key), lastValues.joinToString(","))
        }
    }

    fun add(key: String, value: T, max: Int = -1) {
        add(key, listOf(value), max)
    }

    fun get(key: String): LinkedList<String> {
        val result = LinkedList<String>()
        val values = prefs.getString(getKey(key), null)
        if(values != null) result.addAll(values.split(","))
        return result
    }

    private fun getKey(key: String) = Prefs.LAST_PICKED_PREFIX + key
}

fun <T> LastPickedValuesStore<T>.moveLastPickedGroupableDisplayItemToFront(
    key: String,
    items: LinkedList<GroupableDisplayItem<T>>,
    itemPool: List<GroupableDisplayItem<T>>)
{
    val lastPickedItems = find(get(key), itemPool)
    val reverseIt = lastPickedItems.descendingIterator()
    while (reverseIt.hasNext()) {
        val lastPicked = reverseIt.next()
        if (!items.remove(lastPicked)) items.removeLast()
        items.addFirst(lastPicked)
    }
}

private fun <T> find(values: List<String>, itemPool: Iterable<GroupableDisplayItem<T>>): LinkedList<GroupableDisplayItem<T>> {
    val result = LinkedList<GroupableDisplayItem<T>>()
    for (value in values) {
        val item = find(value, itemPool)
        if(item != null) result.add(item)
    }
    return result
}

private fun <T> find(value: String, itemPool: Iterable<GroupableDisplayItem<T>>): GroupableDisplayItem<T>? {
    for (item in itemPool) {
        val subItems = item.items
        // returns only items which are not groups themselves
        if (subItems != null) {
            val subItem = find(value, subItems.asIterable())
            if (subItem != null) return subItem
        } else if (value == item.value.toString()) {
            return item
        }
    }
    return null
}

fun <T> LastPickedValuesStore<T>.moveLastPickedDisplayItemsToFront(
    key: String,
    items: LinkedList<DisplayItem<T>>,
    itemPool: List<DisplayItem<T>>)
{
    val lastPickedItems = findDisplayItems(get(key), itemPool)
    val reverseIt = lastPickedItems.descendingIterator()
    while (reverseIt.hasNext()) {
        val lastPicked = reverseIt.next()
        if (!items.remove(lastPicked)) items.removeLast()
        items.addFirst(lastPicked)
    }
}

private fun <T> findDisplayItems(values: List<String>, itemPool: Iterable<DisplayItem<T>>): LinkedList<DisplayItem<T>> {
    val result = LinkedList<DisplayItem<T>>()
    for (value in values) {
        val item = itemPool.find { it.value.toString() == value }
        if (item != null) result.add(item)
    }
    return result
}
