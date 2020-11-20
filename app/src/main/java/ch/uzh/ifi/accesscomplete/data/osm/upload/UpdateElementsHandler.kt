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

package ch.uzh.ifi.accesscomplete.data.osm.upload

import de.westnordost.osmapi.common.Handler
import de.westnordost.osmapi.map.changes.DiffElement
import de.westnordost.osmapi.map.data.*
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.ElementKey

/** Reads the answer of an update map call on the OSM API. */
class UpdateElementsHandler : Handler<DiffElement> {
    private val nodeDiffs: MutableMap<Long, DiffElement> = mutableMapOf()
    private val wayDiffs: MutableMap<Long, DiffElement> = mutableMapOf()
    private val relationDiffs: MutableMap<Long, DiffElement> = mutableMapOf()

    override fun handle(d: DiffElement) {
        when (d.type ?: return) {
            Element.Type.NODE -> nodeDiffs[d.clientId] = d
            Element.Type.WAY -> wayDiffs[d.clientId] = d
            Element.Type.RELATION -> relationDiffs[d.clientId] = d
        }
    }

    fun getElementUpdates(elements: Collection<Element>): ElementUpdates {
        val updatedElements = mutableListOf<Element>()
        val deletedElementKeys = mutableListOf<ElementKey>()
        for (element in elements) {
            val update = getDiff(element.type, element.id) ?: continue
            if (update.serverId != null && update.serverVersion != null) {
                updatedElements.add(createUpdatedElement(element, update.serverId, update.serverVersion))
            } else {
                deletedElementKeys.add(ElementKey(update.type, update.clientId))
            }
        }
        return ElementUpdates(updatedElements, deletedElementKeys)
    }

    private fun getDiff(type: Element.Type, id: Long): DiffElement? = when (type) {
        Element.Type.NODE -> nodeDiffs[id]
        Element.Type.WAY -> wayDiffs[id]
        Element.Type.RELATION -> relationDiffs[id]
    }

    private fun createUpdatedElement(element: Element, newId: Long, newVersion: Int): Element =
        when (element) {
            is Node -> createUpdatedNode(element, newId, newVersion)
            is Way -> createUpdatedWay(element, newId, newVersion)
            is Relation -> createUpdatedRelation(element, newId, newVersion)
            else -> throw RuntimeException()
        }

    private fun createUpdatedNode(node: Node, newId: Long, newVersion: Int): Node {
        return OsmNode(newId, newVersion, node.position, node.tags?.let { HashMap(it) })
    }

    private fun createUpdatedWay(way: Way, newId: Long, newVersion: Int): Way {
        val newNodeIds = ArrayList<Long>(way.nodeIds.size)
        for (nodeId in way.nodeIds) {
            val update = nodeDiffs[nodeId]
            if (update == null) newNodeIds.add(nodeId)
            else if (update.serverId != null) newNodeIds.add(update.serverId)
        }
        return OsmWay(newId, newVersion, newNodeIds, way.tags?.let { HashMap(it) })
    }

    private fun createUpdatedRelation(relation: Relation, newId: Long, newVersion: Int): Relation {
        val newRelationMembers = ArrayList<RelationMember>(relation.members.size)
        for (member in relation.members) {
            val update = getDiff(member.type, member.ref)
            if (update == null) newRelationMembers.add(OsmRelationMember(member.ref, member.role, member.type))
            else if(update.serverId != null) newRelationMembers.add(OsmRelationMember(update.serverId, member.role, member.type))
        }
        return OsmRelation(newId, newVersion, newRelationMembers, relation.tags?.let { HashMap(it) })
    }
}

data class ElementUpdates(val updated: Collection<Element>, val deleted: Collection<ElementKey>)
