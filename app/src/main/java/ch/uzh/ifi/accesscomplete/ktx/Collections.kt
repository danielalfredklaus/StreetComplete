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

package ch.uzh.ifi.accesscomplete.ktx

import de.westnordost.osmapi.map.data.LatLon

/** Return the first and last element of this list. If it contains only one element, just that one */
fun <E> List<E>.firstAndLast() = if (size == 1) listOf(first()) else listOf(first(), last())

/** Returns whether the collection contains any of the [elements] */
fun <E> Collection<E>.containsAny(elements: Collection<E>) = elements.any { contains(it) }

/**
 * Starting at [index] (exclusive), iterating the list in reverse, returns the first element that
 * matches the given [predicate], or `null` if no such element was found.
 */
inline fun <T> List<T>.findPrevious(index: Int, predicate: (T) -> Boolean): T? {
    val iterator = this.listIterator(index)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(element)) return element
    }
    return null
}

/**
 * Starting at [index] (inclusive), iterating the list, returns the first element that
 * matches the given [predicate], or `null` if no such element was found.
 */
inline fun <T> List<T>.findNext(index: Int, predicate: (T) -> Boolean): T? {
    val iterator = this.listIterator(index)
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (predicate(element)) return element
    }
    return null
}

/** Iterate through the given list of points in pairs, so [predicate] is called for every line */
inline fun Iterable<LatLon>.forEachLine(predicate: (first: LatLon, second: LatLon) -> Unit) {
    val it = iterator()
    if (!it.hasNext()) return
    var item1 = it.next()
    while (it.hasNext()) {
        val item2 = it.next()
        predicate(item1, item2)
        item1 = item2
    }
}

/** returns the index of the first element yielding the largest value of the given function or -1
 *  if there are no elements. Analogous to the maxBy extension function. */
inline fun <T, R : Comparable<R>> Iterable<T>.indexOfMaxBy(selector: (T) -> R): Int {
    val iterator = iterator()
    if (!iterator.hasNext()) return -1
    var indexOfMaxElem = 0
    var i = 0
    var maxValue = selector(iterator.next())
    while (iterator.hasNext()) {
        ++i
        val v = selector(iterator.next())
        if (maxValue < v) {
            indexOfMaxElem = i
            maxValue = v
        }
    }
    return indexOfMaxElem
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun <T> Collection<T>.containsExactlyInAnyOrder(other: Collection<T>): Boolean =
    other.size == size && containsAll(other)
