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

package ch.uzh.ifi.accesscomplete.data.osm.upload.changesets

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OpenChangesetsDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: OpenChangesetsDao

    private val Q = "Hurzipurz"
    private val P = "Brasliweks"
    private val SOURCE = "test"

    @Before fun createDao() {
        dao = OpenChangesetsDao(dbHelper, OpenChangesetMapping())
    }

    @Test fun deleteNonExistent() {
        assertFalse(dao.delete(Q, SOURCE))
    }

    @Test fun createDelete() {
        dao.put(OpenChangeset(Q, SOURCE, 1))
        assertTrue(dao.delete(Q, SOURCE))
        assertNull(dao.get(Q, SOURCE))
    }

    @Test fun getNull() {
        assertNull(dao.get(Q, SOURCE))
    }

    @Test fun insertChangesetId() {
        dao.put(OpenChangeset(Q, SOURCE, 12))
        val info = dao.get(Q, SOURCE)!!
        assertEquals(12, info.changesetId)
        assertEquals(Q, info.questType)
        assertEquals(SOURCE, info.source)
    }

    @Test fun replaceChangesetId() {
        dao.put(OpenChangeset(Q, SOURCE, 12))
        dao.put(OpenChangeset(Q, SOURCE, 6497))
        assertEquals(6497, dao.get(Q, SOURCE)!!.changesetId)
    }

    @Test fun getNone() {
        assertTrue(dao.getAll().isEmpty())
    }

    @Test fun insertTwo() {
        dao.put(OpenChangeset(Q, SOURCE, 1))
        dao.put(OpenChangeset(P, SOURCE, 2))
        assertEquals(2, dao.getAll().size)
    }
}
