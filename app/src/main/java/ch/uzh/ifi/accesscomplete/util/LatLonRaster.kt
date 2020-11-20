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
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/** A spatial index implemented as a grid, based on points  */
class LatLonRaster(bounds: BoundingBox, private val cellSize: Double) {
    private val raster: Array<ArrayList<LatLon>?>
    private val rasterWidth: Int
    private val rasterHeight: Int
    private val bbox: BoundingBox
    var size = 0
        private set

    init {
        val lonDiff = normalizeLongitude(bounds.maxLongitude - bounds.minLongitude)
        val latDiff = bounds.maxLatitude - bounds.minLatitude
        rasterWidth = ceil(lonDiff / cellSize).toInt()
        rasterHeight = ceil(latDiff / cellSize).toInt()
        raster = arrayOfNulls(rasterWidth * rasterHeight)
        val maxLon = normalizeLongitude(bounds.minLongitude + rasterWidth * cellSize)
        val maxLat = bounds.minLatitude + rasterHeight * cellSize
        bbox = BoundingBox(bounds.min, OsmLatLon(maxLat, maxLon))
    }

    fun insert(p: LatLon) {
        val x = longitudeToCellX(p.longitude)
        val y = latitudeToCellY(p.latitude)
        if(!isInsideBounds(x, y)) return
        var list = raster[y * rasterWidth + x]
        if (list == null) {
            list = ArrayList()
            raster[y * rasterWidth + x] = list
        }
        list.add(p)
        size++
    }

    fun getAll(bounds: BoundingBox): Iterable<LatLon> {
        val startX = max(0, min(longitudeToCellX(bounds.minLongitude), rasterWidth - 1))
        val startY = max(0, min(latitudeToCellY(bounds.minLatitude), rasterHeight - 1))
        val endX = max(0, min(longitudeToCellX(bounds.maxLongitude), rasterWidth - 1))
        val endY = max(0, min(latitudeToCellY(bounds.maxLatitude), rasterHeight - 1))
        val result = MultiIterable<LatLon>()
        for (y in startY..endY) {
            for (x in startX..endX) {
                val list = raster[y * rasterWidth + x]
                if (list != null) result.add(list)
            }
        }
        return result
    }

    fun remove(p: LatLon): Boolean {
        val x = longitudeToCellX(p.longitude)
        val y = latitudeToCellY(p.latitude)
        if (x < 0 || x >= rasterWidth || y < 0 || y >= rasterHeight) return false
        val list = raster[y * rasterWidth + x] ?: return false
        val result = list.remove(p)
        if (result) --size
        return result
    }

    private fun isInsideBounds(x: Int, y: Int): Boolean =
        x in 0 until rasterWidth && y in 0 until rasterHeight

    private fun longitudeToCellX(longitude: Double) =
        floor(normalizeLongitude(longitude - bbox.minLongitude) / cellSize).toInt()

    private fun latitudeToCellY(latitude: Double) =
        floor((latitude - bbox.minLatitude) / cellSize).toInt()
}
