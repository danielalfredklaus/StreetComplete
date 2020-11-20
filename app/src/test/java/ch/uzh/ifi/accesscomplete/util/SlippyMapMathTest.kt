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

import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.OsmLatLon
import org.junit.Assert.*
import org.junit.Test

class SlippyMapMathTest {

    @Test fun `convert bbox to tiles rect and back results in same bbox`() {
        val p = OsmLatLon(53.0, 9.0)
        val tile = p.enclosingTile(15)
        val bbox = tile.asBoundingBox(15)
        assertTrue(bbox.minLatitude <= p.latitude)
        assertTrue(bbox.maxLatitude >= p.latitude)
        assertTrue(bbox.minLongitude <= p.longitude)
        assertTrue(bbox.maxLongitude >= p.longitude)
        val r = bbox.enclosingTilesRect(15)
        val bbox2 = r.asBoundingBox(15)
        assertEquals(bbox, bbox2)
    }

    @Test fun `enclosingTilesRect of bbox that crosses 180th meridian does not`() {
        BoundingBox(10.0, 170.0, 20.0, -170.0).enclosingTilesRect(4)
        // a TilesRect that is initialized crossing 180th meridian would throw an illegal argument
        // exception
    }

    @Test fun `asTileSequence returns sequence of contained tiles`() {
        assertEquals(listOf(
            Tile(1, 1),
            Tile(2, 1),
            Tile(1, 2),
            Tile(2, 2)
        ), TilesRect(1, 1, 2, 2).asTileSequence().toList())
    }

    @Test fun `minTileRect of empty list returns null`() {
        assertNull(listOf<Tile>().minTileRect())
    }

    @Test fun `minTileRect of list with one entry returns tiles rect of size 1`() {
        assertEquals(TilesRect(1,1,1,1), listOf(Tile(1,1)).minTileRect())
    }

    @Test fun `minTileRect returns correct minimum tiles rect`() {
        assertEquals(
            TilesRect(3, 2, 32, 15),
            listOf(
                Tile(5, 8),
                Tile(3, 2),
                Tile(6, 15),
                Tile(32, 12)
            ).minTileRect()
        )
    }

    @Test fun `TilesRect size returns correct size`() {
        assertEquals(12, TilesRect(0, 0, 3, 2).size)
    }
}
