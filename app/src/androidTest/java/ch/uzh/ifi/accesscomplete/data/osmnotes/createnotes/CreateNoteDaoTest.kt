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

package ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.ElementKey
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class CreateNoteDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: CreateNoteDao

    @Before fun createDao() {
        dao = CreateNoteDao(dbHelper, CreateNoteMapping(serializer))
    }

    @Test fun getButNothingIsThere() {
        assertNull(dao.get(1L))
    }

    @Test fun getAllButNothingIsThere() {
        assertEquals(listOf<CreateNote>(), dao.getAll())
    }

    @Test fun addGetAndDelete() {
        val listener = mock(CreateNoteDao.Listener::class.java)
        dao.addListener(listener)

        val note = CreateNote(null, "text", OsmLatLon(3.0, 5.0), "title",
                ElementKey(Element.Type.NODE, 132L), arrayListOf("hello", "hey"))

        assertTrue(dao.add(note))
        verify(listener).onAddedCreateNote()
        val dbNote = dao.get(note.id!!)!!

        assertEquals(note, dbNote)

        assertTrue(dao.delete(note.id!!))
        verify(listener).onDeletedCreateNote()

        assertNull(dao.get(note.id!!))
    }

    @Test fun delete() {
        val listener = mock(CreateNoteDao.Listener::class.java)
        dao.addListener(listener)

        val note = CreateNote(null, "text", OsmLatLon(3.0, 5.0))

        assertTrue(dao.add(note))
        verify(listener).onAddedCreateNote()
        assertTrue(dao.delete(note.id!!))
        verify(listener).onDeletedCreateNote()
        assertNull(dao.get(note.id!!))
    }

    @Test fun addAndGetNullableFields() {
        val listener = mock(CreateNoteDao.Listener::class.java)
        dao.addListener(listener)
        val note = CreateNote(null, "text", OsmLatLon(3.0, 5.0))

        assertTrue(dao.add(note))
        verify(listener).onAddedCreateNote()
        val dbNote = dao.get(note.id!!)!!

        assertNull(dbNote.elementKey)
        assertNull(dbNote.questTitle)
        assertNull(dbNote.imagePaths)
    }

    @Test fun getAll() {
        dao.add(CreateNote(null, "this is in", OsmLatLon(0.5, 0.5)))
        dao.add(CreateNote(null, "this is out", OsmLatLon(-0.5, 0.5)))

        assertEquals(1, dao.getAll(BoundingBox(0.0, 0.0, 1.0, 1.0)).size)
        assertEquals(2, dao.getAll().size)
    }

    @Test fun getAllPositions() {
        dao.add(CreateNote(null, "this is in", OsmLatLon(0.5, 0.5)))
        dao.add(CreateNote(null, "this is out", OsmLatLon(-0.5, 0.5)))

        val positions = dao.getAllPositions(BoundingBox(0.0, 0.0, 1.0, 1.0))
        assertEquals(OsmLatLon(0.5, 0.5), positions.single())
    }

    @Test fun getCount0() {
        assertEquals(0, dao.getCount())
    }

    @Test fun getCount1() {
        dao.add(CreateNote(null, "joho", OsmLatLon(0.5, 0.5)))
        assertEquals(1, dao.getCount())
    }

    @Test fun getCount2() {
        dao.add(CreateNote(null, "joho", OsmLatLon(0.5, 0.5)))
        dao.add(CreateNote(null, "joho", OsmLatLon(0.1, 0.5)))

        assertEquals(2, dao.getCount())
    }
}
