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

    private val q = "Hurzipurz"
    private val p = "Brasliweks"
    private val source = "test"

    @Before fun createDao() {
        dao = OpenChangesetsDao(dbHelper, OpenChangesetMapping())
    }

    @Test fun deleteNonExistent() {
        assertFalse(dao.delete(q, source))
    }

    @Test fun createDelete() {
        dao.put(OpenChangeset(q, source, 1))
        assertTrue(dao.delete(q, source))
        assertNull(dao.get(q, source))
    }

    @Test fun getNull() {
        assertNull(dao.get(q, source))
    }

    @Test fun insertChangesetId() {
        dao.put(OpenChangeset(q, source, 12))
        val info = dao.get(q, source)!!
        assertEquals(12, info.changesetId)
        assertEquals(q, info.questType)
        assertEquals(source, info.source)
    }

    @Test fun replaceChangesetId() {
        dao.put(OpenChangeset(q, source, 12))
        dao.put(OpenChangeset(q, source, 6497))
        assertEquals(6497, dao.get(q, source)!!.changesetId)
    }

    @Test fun getNone() {
        assertTrue(dao.getAll().isEmpty())
    }

    @Test fun insertTwo() {
        dao.put(OpenChangeset(q, source, 1))
        dao.put(OpenChangeset(p, source, 2))
        assertEquals(2, dao.getAll().size)
    }
}
