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

package ch.uzh.ifi.accesscomplete.data.download.tiles

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import ch.uzh.ifi.accesscomplete.util.Tile
import ch.uzh.ifi.accesscomplete.util.TilesRect
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DownloadedTilesDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: DownloadedTilesDao

    @Before fun createDao() {
        dao = DownloadedTilesDao(dbHelper)
    }

    @Test fun putGetOne() {
        dao.put(r(5, 8, 5, 8), "Huhu")
        val huhus = dao.get(r(5, 8, 5, 8), 0)

        assertEquals(1, huhus.size)
        assertTrue(huhus.contains("Huhu"))
    }

    @Test fun putGetOld() {
        dao.put(r(5, 8, 5, 8), "Huhu")
        val huhus = dao.get(r(5, 8, 5, 8), System.currentTimeMillis() + 1000)
        assertTrue(huhus.isEmpty())
    }

    @Test fun putSomeOld() {
        dao.put(r(0, 0, 1, 3), "Huhu")
        Thread.sleep(2000)
        dao.put(r(1, 3, 5, 5), "Huhu")
        val huhus = dao.get(r(0, 0, 2, 2), System.currentTimeMillis() - 1000)
        assertTrue(huhus.isEmpty())
    }

    @Test fun putMoreGetOne() {
        dao.put(r(5, 8, 6, 10), "Huhu")
        assertFalse(dao.get(r(5, 8, 5, 8), 0).isEmpty())
        assertFalse(dao.get(r(6, 10, 6, 10), 0).isEmpty())
    }

    @Test fun putOneGetMore() {
        dao.put(r(5, 8, 5, 8), "Huhu")
        assertTrue(dao.get(r(5, 8, 5, 9), 0).isEmpty())
    }

    @Test fun remove() {
        dao.put(r(0, 0, 3, 3), "Huhu")
        dao.put(r(0, 0, 0, 0), "Haha")
        dao.put(r(1, 1, 3, 3), "Hihi")
        assertEquals(2, dao.remove(Tile(0, 0))) // removes huhu, haha at 0,0
    }

    @Test fun putSeveralQuestTypes() {
        dao.put(r(0, 0, 5, 5), "Huhu")
        dao.put(r(4, 4, 6, 6), "hoho")
        dao.put(r(4, 0, 4, 7), "hihi")

        var check = dao.get(r(0, 0, 2, 2), 0)
        assertEquals(1, check.size)
        assertTrue(check.contains("Huhu"))

        check = dao.get(r(4, 4, 4, 4), 0)
        assertEquals(3, check.size)

        check = dao.get(r(5, 5, 5, 5), 0)
        assertEquals(2, check.size)
        assertTrue(check.contains("hoho"))
        assertTrue(check.contains("Huhu"))

        check = dao.get(r(0, 0, 6, 6), 0)
        assertTrue(check.isEmpty())
    }

    private fun r(left: Int, top: Int, right: Int, bottom: Int) = TilesRect(left, top, right, bottom)
}
