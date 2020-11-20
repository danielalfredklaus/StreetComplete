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

import org.junit.Before
import org.junit.Test

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Node
import de.westnordost.osmapi.map.data.OsmNode
import de.westnordost.osmapi.map.data.OsmRelation
import de.westnordost.osmapi.map.data.OsmRelationMember
import de.westnordost.osmapi.map.data.OsmWay
import de.westnordost.osmapi.map.data.Relation
import de.westnordost.osmapi.map.data.Way
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on

import org.mockito.Mockito.*

class MergedElementDaoTest {
    private lateinit var nodeDao: NodeDao
    private lateinit var wayDao: WayDao
    private lateinit var relationDao: RelationDao
    private lateinit var dao: MergedElementDao

    @Before fun setUp() {
        nodeDao = mock()
        wayDao = mock()
        relationDao = mock()
        dao = MergedElementDao(nodeDao, wayDao, relationDao)
    }

    @Test fun putNode() {
        val node: Node = mock()
        on(node.type).thenReturn(Element.Type.NODE)
        dao.put(node)
        verify(nodeDao).put(node)
    }

    @Test fun getNode() {
        val node: Node = mock()
        on(node.id).thenReturn(1L)
        dao.get(Element.Type.NODE, 1L)
        verify(nodeDao).get(1L)
    }

    @Test fun deleteNode() {
        dao.delete(Element.Type.NODE, 1L)
        verify(nodeDao).delete(1L)
    }

    @Test fun putWay() {
        val way: Way = mock()
        on(way.type).thenReturn(Element.Type.WAY)
        dao.put(way)
        verify(wayDao).put(way)
    }

    @Test fun getWay() {
        val way: Way = mock()
        on(way.id).thenReturn(1L)

        dao.get(Element.Type.WAY, 1L)
        verify(wayDao).get(1L)
    }

    @Test fun deleteWay() {
        dao.delete(Element.Type.WAY, 1L)
        verify(wayDao).delete(1L)
    }

    @Test fun putRelation() {
        val relation: Relation = mock()
        on(relation.type).thenReturn(Element.Type.RELATION)
        dao.put(relation)
        verify(relationDao).put(relation)
    }

    @Test fun getRelation() {
        val relation: Relation = mock()
        on(relation.id).thenReturn(1L)

        dao.get(Element.Type.RELATION, 1L)
        verify(relationDao).get(1L)
    }

    @Test fun deleteRelation() {
        dao.delete(Element.Type.RELATION, 1L)
        verify(relationDao).delete(1L)
    }

    @Test fun putAllRelations() {
        dao.putAll(listOf(createARelation()))
        verify(relationDao).putAll(anyCollection())
    }

    @Test fun putAllWays() {
        dao.putAll(listOf(createAWay()))
        verify(wayDao).putAll(anyCollection())
    }

    @Test fun putAllNodes() {
        dao.putAll(listOf(createANode()))
        verify(nodeDao).putAll(anyCollection())
    }

    @Test fun putAllElements() {
        dao.putAll(listOf(createANode(), createAWay(), createARelation()))

        verify(nodeDao).putAll(anyCollection())
        verify(wayDao).putAll(anyCollection())
        verify(relationDao).putAll(anyCollection())
    }

    private fun createANode() = OsmNode(0, 0, 0.0, 0.0, null)

    private fun createAWay() = OsmWay(0, 0, listOf(0L), null)

    private fun createARelation() =
        OsmRelation(0, 0, listOf(OsmRelationMember(0, "", Element.Type.NODE)), null)

    @Test fun deleteUnreferenced() {
        dao.deleteUnreferenced()
        verify(nodeDao).deleteUnreferenced()
        verify(wayDao).deleteUnreferenced()
        verify(relationDao).deleteUnreferenced()
    }
}
