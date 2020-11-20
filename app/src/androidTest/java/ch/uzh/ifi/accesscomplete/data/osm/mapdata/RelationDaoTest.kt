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
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmRelation
import de.westnordost.osmapi.map.data.OsmRelationMember
import de.westnordost.osmapi.map.data.Relation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RelationDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: RelationDao

    @Before fun createDao() {
        dao = RelationDao(dbHelper, RelationMapping(serializer))
    }

    @Test fun putGetNoTags() {
        val members = listOf(
            OsmRelationMember(0, "outer", Element.Type.WAY),
            OsmRelationMember(1, "inner", Element.Type.WAY)
        )
        val relation = OsmRelation(5, 1, members, null)
        dao.put(relation)
        val dbRelation = dao.get(5)

        checkEqual(relation, dbRelation!!)
    }

    @Test fun putGetWithTags() {
        val members = listOf(
            OsmRelationMember(0, "outer", Element.Type.WAY),
            OsmRelationMember(1, "inner", Element.Type.WAY)
        )
        val relation = OsmRelation(5, 1, members, mapOf("a key" to "a value"))
        dao.put(relation)
        val dbRelation = dao.get(5)

        checkEqual(relation, dbRelation!!)
    }

    private fun checkEqual(relation: Relation, dbRelation: Relation) {
        assertEquals(relation.id, dbRelation.id)
        assertEquals(relation.version.toLong(), dbRelation.version.toLong())
        assertEquals(relation.tags, dbRelation.tags)
        assertEquals(relation.members, dbRelation.members)
    }
}
