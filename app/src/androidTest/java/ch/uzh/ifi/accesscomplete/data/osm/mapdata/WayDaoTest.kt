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
import de.westnordost.osmapi.map.data.OsmWay
import de.westnordost.osmapi.map.data.Way
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WayDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: WayDao

    @Before fun createDao() {
        dao = WayDao(dbHelper, WayMapping(serializer))
    }

    @Test fun putGetNoTags() {
        val way = OsmWay(5, 1, listOf(1L, 2L, 3L, 4L), null)
        dao.put(way)
        val dbWay = dao.get(5)

        checkEqual(way, dbWay!!)
    }

    @Test fun putGetWithTags() {
        val way = OsmWay(5, 1, listOf(1L, 2L, 3L, 4L), mapOf("a key" to "a value"))
        dao.put(way)
        val dbWay = dao.get(5)

        checkEqual(way, dbWay!!)
    }

    private fun checkEqual(way: Way, dbWay: Way) {
        assertEquals(way.id, dbWay.id)
        assertEquals(way.version, dbWay.version)
        assertEquals(way.nodeIds, dbWay.nodeIds)
        assertEquals(way.tags, dbWay.tags)
    }
}
