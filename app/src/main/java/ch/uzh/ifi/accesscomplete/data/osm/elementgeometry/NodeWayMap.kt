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

package ch.uzh.ifi.accesscomplete.data.osm.elementgeometry


/** Knows which vertices connect which ways. T is the identifier of a vertex  */
class NodeWayMap<T>(ways: List<List<T>>) {
    private val wayEndpoints = LinkedHashMap<T, MutableList<List<T>>>()

    init {
        for (way in ways) {
            val firstNode = way.first()
            val lastNode = way.last()

            wayEndpoints.getOrPut(firstNode, { ArrayList() }).add(way)
            wayEndpoints.getOrPut(lastNode, { ArrayList() }).add(way)
        }
    }

    fun hasNextNode(): Boolean = wayEndpoints.isNotEmpty()

    fun getNextNode(): T = wayEndpoints.keys.iterator().next()

    fun getWaysAtNode(node: T): List<List<T>>? = wayEndpoints[node]

    fun removeWay(way: List<T>) {
        val it = wayEndpoints.values.iterator()
        while (it.hasNext()) {
            val waysPerNode = it.next()

            val waysIt = waysPerNode.iterator()
            while (waysIt.hasNext()) {
                if (waysIt.next() === way) {
                    waysIt.remove()
                }
            }

            if (waysPerNode.isEmpty()) {
                it.remove()
            }
        }
    }
}
