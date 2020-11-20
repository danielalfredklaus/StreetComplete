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

package ch.uzh.ifi.accesscomplete.data.osm.mapdata

import javax.inject.Inject

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Node
import de.westnordost.osmapi.map.data.Relation
import de.westnordost.osmapi.map.data.Way

/** Stores OSM elements */
class MergedElementDao @Inject
constructor(
        private val nodeDao: NodeDao,
        private val wayDao: WayDao,
        private val relationDao: RelationDao
) {

    fun putAll(elements: Collection<Element>) {
        val nodes = mutableListOf<Node>()
        val ways = mutableListOf<Way>()
        val relations = mutableListOf<Relation>()

        for (element in elements) {
            when (element) {
                is Node -> nodes.add(element)
                is Way -> ways.add(element)
                is Relation -> relations.add(element)
            }
        }
        if (nodes.isNotEmpty()) nodeDao.putAll(nodes)
        if (ways.isNotEmpty()) wayDao.putAll(ways)
        if (relations.isNotEmpty()) relationDao.putAll(relations)
    }

    fun put(element: Element) {
        when (element) {
            is Node -> nodeDao.put(element)
            is Way -> wayDao.put(element)
            is Relation -> relationDao.put(element)
        }
    }

    fun delete(type: Element.Type, id: Long) {
        when (type) {
            Element.Type.NODE -> nodeDao.delete(id)
            Element.Type.WAY -> wayDao.delete(id)
            Element.Type.RELATION -> relationDao.delete(id)
        }
    }

    fun get(type: Element.Type, id: Long): Element? {
        return when (type) {
            Element.Type.NODE -> nodeDao.get(id)
            Element.Type.WAY -> wayDao.get(id)
            Element.Type.RELATION -> relationDao.get(id)
        }
    }

    fun deleteUnreferenced() {
        nodeDao.deleteUnreferenced()
        wayDao.deleteUnreferenced()
        relationDao.deleteUnreferenced()
    }
}
