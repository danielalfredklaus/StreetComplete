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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import ch.uzh.ifi.osmapi.map.MutableMapData
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometryCreator
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/** MapDataWithGeometry that lazily creates the element geometry. Will create incomplete (relation)
 *  geometry */
class CachingMapDataWithGeometry @Inject constructor(
    private val elementGeometryCreator: ElementGeometryCreator
) : MutableMapData(), MapDataWithGeometry {

    private val nodeGeometriesById: ConcurrentHashMap<Long, Optional<ElementPointGeometry>> = ConcurrentHashMap()
    private val wayGeometriesById: ConcurrentHashMap<Long, Optional<ElementGeometry>> = ConcurrentHashMap()
    private val relationGeometriesById: ConcurrentHashMap<Long, Optional<ElementGeometry>> = ConcurrentHashMap()

    override fun getNodeGeometry(id: Long): ElementPointGeometry? {
        val node = nodesById[id] ?: return null
        return nodeGeometriesById.getOrPut(id, {
            Optional(elementGeometryCreator.create(node))
        }).value
    }

    override fun getWayGeometry(id: Long): ElementGeometry? {
        val way = waysById[id] ?: return null
        return wayGeometriesById.getOrPut(id, {
            Optional(elementGeometryCreator.create(way, this, true))
        }).value
    }

    override fun getRelationGeometry(id: Long): ElementGeometry? {
        val relation = relationsById[id] ?: return null
        return relationGeometriesById.getOrPut(id, {
            Optional(elementGeometryCreator.create(relation, this, true))
        }).value
    }
}

// workaround the limitation of ConcurrentHashMap that it cannot store null values by wrapping it
private class Optional<T>(val value: T?)
