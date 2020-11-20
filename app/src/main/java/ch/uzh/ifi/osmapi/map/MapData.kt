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

package ch.uzh.ifi.osmapi.map

import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.util.MultiIterable
import de.westnordost.osmapi.map.data.*
import de.westnordost.osmapi.map.handler.MapDataHandler

interface MapDataWithGeometry : MapData {
    fun getNodeGeometry(id: Long): ElementPointGeometry?
    fun getWayGeometry(id: Long): ElementGeometry?
    fun getRelationGeometry(id: Long): ElementGeometry?

    fun getGeometry(elementType: Element.Type, id: Long): ElementGeometry? = when(elementType) {
        Element.Type.NODE -> getNodeGeometry(id)
        Element.Type.WAY -> getWayGeometry(id)
        Element.Type.RELATION -> getRelationGeometry(id)
    }
}

interface MapData : Iterable<Element> {
    val nodes: Collection<Node>
    val ways: Collection<Way>
    val relations: Collection<Relation>
    val boundingBox: BoundingBox?

    fun getNode(id: Long): Node?
    fun getWay(id: Long): Way?
    fun getRelation(id: Long): Relation?
}

open class MutableMapData : MapData, MapDataHandler {

    protected val nodesById: MutableMap<Long, Node> = mutableMapOf()
    protected val waysById: MutableMap<Long, Way> = mutableMapOf()
    protected val relationsById: MutableMap<Long, Relation> = mutableMapOf()
    override var boundingBox: BoundingBox? = null
    protected set

    override fun handle(bounds: BoundingBox) { boundingBox = bounds }
    override fun handle(node: Node) { nodesById[node.id] = node }
    override fun handle(way: Way) { waysById[way.id] = way }
    override fun handle(relation: Relation) { relationsById[relation.id] = relation }

    override val nodes get() = nodesById.values
    override val ways get() = waysById.values
    override val relations get() = relationsById.values

    override fun getNode(id: Long) = nodesById[id]
    override fun getWay(id: Long) = waysById[id]
    override fun getRelation(id: Long) = relationsById[id]

    fun addAll(elements: Iterable<Element>) {
        for (element in elements) {
            when(element) {
                is Node -> nodesById[element.id] = element
                is Way -> waysById[element.id] = element
                is Relation -> relationsById[element.id] = element
            }
        }
    }

    override fun iterator(): Iterator<Element> {
        val elements = MultiIterable<Element>()
        elements.add(nodes)
        elements.add(ways)
        elements.add(relations)
        return elements.iterator()
    }
}

fun MapData.isRelationComplete(id: Long): Boolean =
    getRelation(id)?.members?.all { member ->
        when (member.type!!) {
            Element.Type.NODE -> getNode(member.ref) != null
            Element.Type.WAY -> getWay(member.ref) != null && isWayComplete(member.ref)
            /* not being recursive here is deliberate. sub-relations are considered not relevant
               for the element geometry in StreetComplete (and OSM API call to get a "complete"
               relation also does not include sub-relations) */
            Element.Type.RELATION -> getRelation(member.ref) != null
        }
    } ?: false

fun MapData.isWayComplete(id: Long): Boolean =
    getWay(id)?.nodeIds?.all { getNode(it) != null } ?: false
