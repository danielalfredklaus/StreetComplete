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

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import de.westnordost.osmapi.map.data.Node
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmNode
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NodeDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: NodeDao

    @Before fun createDao() {
        dao = NodeDao(dbHelper, NodeMapping(serializer))
    }

    @Test fun putGetNoTags() {
        val pos = OsmLatLon(2.0, 2.0)
        val node = OsmNode(5, 1, pos, null)
        dao.put(node)
        val dbNode = dao.get(5)

        checkEqual(node, dbNode!!)
    }

    @Test fun putGetWithTags() {
        val pos = OsmLatLon(2.0, 2.0)
        val node = OsmNode(5, 1, pos, mapOf("a key" to "a value"))
        dao.put(node)
        val dbNode = dao.get(5)

        checkEqual(node, dbNode!!)
    }

    private fun checkEqual(node: Node, dbNode: Node) {
        assertEquals(node.id, dbNode.id)
        assertEquals(node.version.toLong(), dbNode.version.toLong())
        assertEquals(node.position, dbNode.position)
        assertEquals(node.tags, dbNode.tags)
    }
}
