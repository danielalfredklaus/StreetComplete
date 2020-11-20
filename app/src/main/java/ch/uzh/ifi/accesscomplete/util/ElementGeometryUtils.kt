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

import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import ch.uzh.ifi.accesscomplete.ktx.forEachLine
import kotlin.math.abs

fun ElementPolylinesGeometry.getOrientationAtCenterLineInDegrees(): Float {
    val centerLine = polylines.first().centerLineOfPolyline()
    return centerLine.first.initialBearingTo(centerLine.second).toFloat()
}

/** Returns whether any individual line segment in this ElementPolylinesGeometry is both within
 *  [maxDistance]m of any line segments of [others] and also
 *  and "aligned", meaning
 *
 *  Warning: This is computationally very expensive ( for normal ways, O(n³) ), avoid if possible */
fun ElementPolylinesGeometry.isNearAndAligned(
    maxDistance: Double,
    maxAngle: Double,
    others: Iterable<ElementPolylinesGeometry>
): Boolean {
    val bounds = getBounds().enlargedBy(maxDistance)
    return others.any { other ->
        bounds.intersect(other.getBounds()) &&
        polylines.any { polyline ->
            other.polylines.any { otherPolyline ->
                polyline.isWithinDistanceAndAngleOf(otherPolyline, maxDistance, maxAngle)
            }
        }
    }
}

private fun List<LatLon>.isWithinDistanceAndAngleOf(other: List<LatLon>, maxDistance: Double, maxAngle: Double): Boolean {
    forEachLine { first, second ->
        other.forEachLine { otherFirst, otherSecond ->
            val bearing = first.initialBearingTo(second)
            val otherBearing = otherFirst.initialBearingTo(otherSecond)
            val bearingDiff = abs((bearing - otherBearing).normalizeDegrees(-180.0))
            // two ways directly opposite each other should count as aligned
            val alignmentDiff = if (bearingDiff > 90) 180 - bearingDiff else bearingDiff
            val distance = first.distanceToArc(otherFirst, otherSecond)
            if (alignmentDiff <= maxAngle && distance <= maxDistance)
                return true
        }
    }
    return false
}
