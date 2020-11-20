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

package ch.uzh.ifi.accesscomplete.data.osm.splitway

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.TestQuestType
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.TestQuestType2
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import de.westnordost.osmapi.map.data.OsmLatLon
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class OsmQuestSplitWayDaoTest : ApplicationDbTestCase() {

    private val questType = TestQuestType()
    private val questType2 = TestQuestType2()
    private lateinit var dao: OsmQuestSplitWayDao

    @Before fun createDao() {
        val mapping = OsmQuestSplitWayMapping(serializer, QuestTypeRegistry(listOf(questType, questType2)))
        dao = OsmQuestSplitWayDao(dbHelper, mapping)
    }

    @Test fun getButNothingIsThere() {
        assertNull(dao.get(1L))
    }

    @Test fun getAllButNothingIsThere() {
        assertEquals(listOf<OsmQuestSplitWay>(), dao.getAll())
    }

    @Test fun addAndGet() {
        val listener = mock(OsmQuestSplitWayDao.Listener::class.java)
        dao.addListener(listener)

        val id = 1L
        val input = createOsmQuestSplitWay(id)
        dao.add(input)
        verify(listener).onAddedSplitWay()
        val output = dao.get(id)!!

        assertEquals(input.questType, output.questType)
        assertEquals(input.wayId, output.wayId)
        assertEquals(input.questId, output.questId)
        assertEquals(input.splits.size, output.splits.size)
        assertEquals(input.questTypesOnWay, output.questTypesOnWay)
        val it = input.splits.listIterator()
        val ot = output.splits.listIterator()
        while(it.hasNext()) {
            val iSplit = it.next()
            val oSplit = ot.next()
            assertEquals(iSplit, oSplit)
        }
    }

    @Test fun delete() {
        val listener: OsmQuestSplitWayDao.Listener = mock(OsmQuestSplitWayDao.Listener::class.java)
        dao.addListener(listener)

        val id = 1L
        assertFalse(dao.delete(id))
        val input = createOsmQuestSplitWay(id)
        dao.add(input)
        verify(listener).onAddedSplitWay()
        assertTrue(dao.delete(id))
        verify(listener).onDeletedSplitWay()
        assertNull(dao.get(id))
    }

    @Test(expected = Exception::class)
    fun addingTwiceThrowsException() {
        val id = 1L
        dao.add(createOsmQuestSplitWay(id, 1L))
        dao.add(createOsmQuestSplitWay(id, 123L))
    }

    @Test fun getAll() {
        dao.add(createOsmQuestSplitWay(1L, 1L))
        dao.add(createOsmQuestSplitWay(2L, 2L))
        assertEquals(2, dao.getAll().size)
    }

    @Test fun getCount0() {
        assertEquals(0, dao.getCount())
    }

    @Test fun getCount1() {
        dao.add(createOsmQuestSplitWay(1L, 1L))
        assertEquals(1, dao.getCount())
    }

    @Test fun getCount2() {
        dao.add(createOsmQuestSplitWay(1L, 1L))
        dao.add(createOsmQuestSplitWay(2L, 2L))
        assertEquals(2, dao.getCount())
    }

    private fun createOsmQuestSplitWay(id: Long, wayId: Long = 1L): OsmQuestSplitWay {
        val pos1 = OsmLatLon(0.0, 0.0)
        val pos2 = OsmLatLon(1.0, 0.0)
        return OsmQuestSplitWay(id, questType, wayId, "test", listOf(
                SplitAtLinePosition(pos1, pos2, 0.3),
                SplitAtPoint(pos2)),
                listOf(questType, questType2)
        )
    }
}


