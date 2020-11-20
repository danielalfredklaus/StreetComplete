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

import java.io.Serializable

import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.util.enclosingBoundingBox

/** Information on the geometry of a quest  */
sealed class ElementGeometry : Serializable {
    abstract val center: LatLon
    // the bbox should not be serialized, his is why the bounds cannot be a (computed) property directly
    abstract fun getBounds(): BoundingBox
}

data class ElementPolylinesGeometry(val polylines: List<List<LatLon>>, override val center: LatLon) : ElementGeometry() {
    @delegate:Transient private val bbox by lazy { polylines.flatten().enclosingBoundingBox() }
    override fun getBounds(): BoundingBox = bbox
}

data class ElementPolygonsGeometry(val polygons: List<List<LatLon>>, override val center: LatLon) : ElementGeometry() {
    @delegate:Transient private val bbox by lazy { polygons.flatten().enclosingBoundingBox() }
    override fun getBounds(): BoundingBox = bbox
}

data class ElementPointGeometry(override val center: LatLon) : ElementGeometry() {
    @delegate:Transient private val bbox by lazy { listOf(center).enclosingBoundingBox() }
    override fun getBounds(): BoundingBox = bbox
}
