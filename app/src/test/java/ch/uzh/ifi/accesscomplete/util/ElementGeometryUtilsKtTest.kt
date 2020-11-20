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

import de.westnordost.osmapi.map.data.OsmLatLon
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import org.junit.Assert.assertTrue

import org.junit.Test

class ElementGeometryUtilsKtTest {

    @Test fun `issue2248 simple`() {
        // https://github.com/westnordost/StreetComplete/issues/2248
        // this is the geometry of the street and sidewalk boiled down to the necessary elements
        // to reproduce this bug
        val street19801348 = ElementPolylinesGeometry(listOf(listOf(
            OsmLatLon(50.0751820, 19.8861837),
            OsmLatLon(50.0751033, 19.8865969)
        )), OsmLatLon(50.0751820, 19.8861837))

        val sidewalk406543797 = ElementPolylinesGeometry(listOf(listOf(
            OsmLatLon(50.0750588, 19.8866672),
            OsmLatLon(50.0751290, 19.8862832)
        )), OsmLatLon(50.0751290, 19.8862832))

        assertTrue(street19801348.isNearAndAligned(10.0, 25.0, listOf(sidewalk406543797)))
    }
}
