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

import org.junit.Test


import org.junit.Assert.*

class NodeWayMapTest {
    @Test fun all() {
        val way1 = listOf(1L, 2L, 3L)
        val way2 = listOf(3L, 4L, 1L)
        val ring = listOf(5L, 1L, 6L, 5L)

        val map = NodeWayMap(listOf(way1, way2, ring))

        assertTrue(map.hasNextNode())
        assertEquals(2, map.getWaysAtNode(1L)?.size)
        assertEquals(2, map.getWaysAtNode(3L)?.size)
        assertEquals(2, map.getWaysAtNode(5L)?.size)
        assertNull(map.getWaysAtNode(2L))

        map.removeWay(way1)
        assertEquals(1, map.getWaysAtNode(1L)?.size)
        assertEquals(1, map.getWaysAtNode(3L)?.size)

        map.removeWay(way2)
        assertNull(map.getWaysAtNode(1L))
        assertNull(map.getWaysAtNode(3L))

        assertTrue(map.hasNextNode())
        assertEquals(5L, map.getNextNode())

        map.removeWay(ring)

        assertFalse(map.hasNextNode())
    }
}
