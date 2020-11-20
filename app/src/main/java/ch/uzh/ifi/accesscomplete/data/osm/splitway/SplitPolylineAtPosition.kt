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

package ch.uzh.ifi.accesscomplete.data.osm.splitway

import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import ch.uzh.ifi.accesscomplete.util.measuredLength
import ch.uzh.ifi.accesscomplete.util.pointOnPolylineFromStart

/** Contains information about at which position to split a way. */
sealed class SplitPolylineAtPosition {
    abstract val pos: LatLon
}

/** When intending to split a way at a node, indicates the precise position of that node */
data class SplitAtPoint(override val pos: OsmLatLon) : SplitPolylineAtPosition()

/** When intending to split a way at a position between two nodes, indicates the precise position
 *  of these two nodes  */
data class SplitAtLinePosition(val pos1: OsmLatLon, val pos2: OsmLatLon, val delta: Double) : SplitPolylineAtPosition() {
    override val pos: LatLon get() {
        val line = listOf(pos1, pos2)
        return line.pointOnPolylineFromStart(line.measuredLength() * delta)!!
    }
    init {
        if(delta <= 0 || delta >= 1)
            throw IllegalArgumentException("Delta must be between 0 and 1 (both exclusive)")
    }
}
