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

import de.westnordost.osmapi.map.data.OsmRelation
import de.westnordost.osmapi.map.data.OsmWay
import de.westnordost.osmapi.map.data.Way
import org.junit.Assert.*
import org.junit.Test

class ElementTest {
    @Test fun `relation with no tags is no area`() {
        assertFalse(OsmRelation(0, 0, null, null).isArea())
    }

    @Test fun `way is closed`() {
        assertTrue(createRing(null).isClosed())
    }

    @Test fun `way is not closed`() {
        assertFalse(createWay(null).isClosed())
    }

    @Test fun `multipolygon relation is an area`() {
        assertTrue(OsmRelation(0, 0, null, mapOf("type" to "multipolygon")).isArea())
    }

    @Test fun `way with no tags is no area`() {
        assertFalse(createWay(null).isArea())
        assertFalse(createRing(null).isArea())
    }

    @Test fun `simple way with area=yes tag is no area`() {
        assertFalse(createWay(mapOf("area" to "yes")).isArea())
    }

    @Test fun `closed way with area=yes tag is an area`() {
        assertTrue(createRing(mapOf("area" to "yes")).isArea())
    }

    @Test fun `closed way with specific value of a key that is usually no area is an area`() {
        assertFalse(createRing(mapOf("railway" to "something")).isArea())
        assertTrue(createRing(mapOf("railway" to "station")).isArea())
    }

    @Test fun `closed way with a certain tag value is an area`() {
        assertFalse(createRing(mapOf("waterway" to "duck")).isArea())
        assertTrue(createRing(mapOf("waterway" to "dock")).isArea())
    }

    private fun createWay(tags: Map<String, String>?): Way = OsmWay(0, 0, listOf(0L, 1L), tags)
    private fun createRing(tags: Map<String, String>?): Way = OsmWay(0, 0, listOf(0L, 1L, 0L), tags)
}
