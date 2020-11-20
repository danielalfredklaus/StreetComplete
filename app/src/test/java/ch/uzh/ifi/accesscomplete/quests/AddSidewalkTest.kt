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

import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmWay
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryAdd
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import ch.uzh.ifi.accesscomplete.quests.sidewalk.AddSidewalk
import ch.uzh.ifi.accesscomplete.quests.sidewalk.SeparatelyMapped
import ch.uzh.ifi.accesscomplete.quests.sidewalk.SidewalkSides
import ch.uzh.ifi.accesscomplete.util.translate
import org.junit.Assert.assertEquals
import org.junit.Test

class AddSidewalkTest {

    private val questType = AddSidewalk()

    @Test fun `applicable to road with missing sidewalk`() {
        val mapData = TestMapDataWithGeometry(listOf(
            OsmWay(1L, 1, listOf(1,2,3), mapOf(
                "highway" to "primary",
                "lit" to "yes"
            ))
        ))
        assertEquals(1, questType.getApplicableElements(mapData).toList().size)
    }

    @Test fun `not applicable to road with nearby footway`() {
        val mapData = TestMapDataWithGeometry(listOf(
            OsmWay(1L, 1, listOf(1,2), mapOf(
                "highway" to "primary",
                "lit" to "yes",
                "width" to "18"
            )),
            OsmWay(2L, 1, listOf(3,4), mapOf(
                "highway" to "footway"
            ))
        ))
        val p1 = OsmLatLon(0.0,0.0)
        val p2 = p1.translate(50.0, 45.0)
        val p3 = p1.translate(14.0, 135.0)
        val p4 = p3.translate(50.0, 45.0)

        mapData.wayGeometriesById[1L] = ElementPolylinesGeometry(listOf(listOf(p1, p2)), p1)
        mapData.wayGeometriesById[2L] = ElementPolylinesGeometry(listOf(listOf(p3, p4)), p3)

        assertEquals(0, questType.getApplicableElements(mapData).toList().size)
    }

    @Test fun `applicable to road with nearby footway that is not aligned to the road`() {
        val mapData = TestMapDataWithGeometry(listOf(
            OsmWay(1L, 1, listOf(1,2), mapOf(
                "highway" to "primary",
                "lit" to "yes",
                "width" to "18"
            )),
            OsmWay(2L, 1, listOf(3,4), mapOf(
                "highway" to "footway"
            ))
        ))
        val p1 = OsmLatLon(0.0,0.0)
        val p2 = p1.translate(50.0, 45.0)
        val p3 = p1.translate(10.0, 135.0)
        val p4 = p3.translate(50.0, 75.0)

        mapData.wayGeometriesById[1L] = ElementPolylinesGeometry(listOf(listOf(p1, p2)), p1)
        mapData.wayGeometriesById[2L] = ElementPolylinesGeometry(listOf(listOf(p3, p4)), p3)

        assertEquals(1, questType.getApplicableElements(mapData).toList().size)
    }

    @Test fun `applicable to road with footway that is far away enough`() {
        val mapData = TestMapDataWithGeometry(listOf(
            OsmWay(1L, 1, listOf(1,2), mapOf(
                "highway" to "primary",
                "lit" to "yes",
                "width" to "18"
            )),
            OsmWay(2L, 1, listOf(3,4), mapOf(
                "highway" to "footway"
            ))
        ))
        val p1 = OsmLatLon(0.0,0.0)
        val p2 = p1.translate(50.0, 45.0)
        val p3 = p1.translate(16.0, 135.0)
        val p4 = p3.translate(50.0, 45.0)

        mapData.wayGeometriesById[1L] = ElementPolylinesGeometry(listOf(listOf(p1, p2)), p1)
        mapData.wayGeometriesById[2L] = ElementPolylinesGeometry(listOf(listOf(p3, p4)), p3)

        assertEquals(1, questType.getApplicableElements(mapData).toList().size)
    }

    @Test fun `applicable to small road with footway that is far away enough`() {
        val mapData = TestMapDataWithGeometry(listOf(
            OsmWay(1L, 1, listOf(1,2), mapOf(
                "highway" to "primary",
                "lit" to "yes",
                "lanes" to "2"
            )),
            OsmWay(2L, 1, listOf(3,4), mapOf(
                "highway" to "cycleway"
            ))
        ))
        val p1 = OsmLatLon(0.0,0.0)
        val p2 = p1.translate(50.0, 45.0)
        val p3 = p1.translate(10.0, 135.0)
        val p4 = p3.translate(50.0, 45.0)

        mapData.wayGeometriesById[1L] = ElementPolylinesGeometry(listOf(listOf(p1, p2)), p1)
        mapData.wayGeometriesById[2L] = ElementPolylinesGeometry(listOf(listOf(p3, p4)), p3)

        assertEquals(1, questType.getApplicableElements(mapData).toList().size)
    }

    @Test fun `apply no sidewalk answer`() {
        questType.verifyAnswer(
            SidewalkSides(left = false, right = false),
            StringMapEntryAdd("sidewalk", "none")
        )
    }

    @Test fun `apply sidewalk left answer`() {
        questType.verifyAnswer(
            SidewalkSides(left = true, right = false),
            StringMapEntryAdd("sidewalk", "left")
        )
    }

    @Test fun `apply sidewalk right answer`() {
        questType.verifyAnswer(
            SidewalkSides(left = false, right = true),
            StringMapEntryAdd("sidewalk", "right")
        )
    }

    @Test fun `apply sidewalk on both sides answer`() {
        questType.verifyAnswer(
            SidewalkSides(left = true, right = true),
            StringMapEntryAdd("sidewalk", "both")
        )
    }

    @Test fun `apply separate sidewalk answer`() {
        questType.verifyAnswer(
            SeparatelyMapped,
            StringMapEntryAdd("sidewalk", "separate")
        )
    }
}
