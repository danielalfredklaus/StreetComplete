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

package ch.uzh.ifi.accesscomplete.map.tangram

import com.mapzen.tangram.LngLat
import com.mapzen.tangram.geometry.Geometry
import com.mapzen.tangram.geometry.Point
import com.mapzen.tangram.geometry.Polygon
import com.mapzen.tangram.geometry.Polyline
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolygonsGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry

fun ElementGeometry.toTangramGeometry(): List<Geometry> = when(this) {
    is ElementPolylinesGeometry -> {
        polylines.map { polyline ->
            Polyline(polyline.map { it.toLngLat() }, mapOf("type" to "line"))
        }
    }
    is ElementPolygonsGeometry -> {
        listOf(
            Polygon(
                polygons.map { polygon ->
                    polygon.map { it.toLngLat() }
                },
                mapOf("type" to "poly")
            )
        )
    }
    is ElementPointGeometry -> {
        listOf(Point(center.toLngLat(), mapOf("type" to "point")))
    }
}

fun ElementPolylinesGeometry.toTangramGeometryWithDirectionIndicator(): List<Geometry> {
    return polylines.map { polyline ->
        Polyline(polyline.map { it.toLngLat() }, mapOf("type" to "arrows"))
    }
}

fun LngLat.toLatLon(): LatLon = OsmLatLon(latitude, longitude)

fun LatLon.toLngLat(): LngLat = LngLat(longitude, latitude)
