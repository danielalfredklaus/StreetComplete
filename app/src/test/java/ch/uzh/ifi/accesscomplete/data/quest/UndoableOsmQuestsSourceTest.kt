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

package ch.uzh.ifi.accesscomplete.data.quest

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuest
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestController
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.invocation.InvocationOnMock
import java.util.*

class UndoableOsmQuestsSourceTest {

    private lateinit var osmQuestController: OsmQuestController
    private lateinit var questStatusListener: OsmQuestController.QuestStatusListener

    private lateinit var listener: UndoableOsmQuestsCountListener

    private lateinit var source: UndoableOsmQuestsSource

    private val baseCount = 10

    @Before fun setUp() {
        osmQuestController = mock()
        on(osmQuestController.addQuestStatusListener(any())).then { invocation: InvocationOnMock ->
            questStatusListener = invocation.arguments[0] as OsmQuestController.QuestStatusListener
            Unit
        }

        on(osmQuestController.getAllUndoableCount()).thenReturn(baseCount)

        source = UndoableOsmQuestsSource(osmQuestController)

        listener = mock()
        source.addListener(listener)
    }

    @Test fun count() {
        assertEquals(baseCount, source.count)
    }


    @Test fun `remove undoable osm quest triggers listener`() {
        questStatusListener.onRemoved(1L, QuestStatus.ANSWERED)
        questStatusListener.onRemoved(2L, QuestStatus.HIDDEN)
        questStatusListener.onRemoved(3L, QuestStatus.CLOSED)
        verifyDecreasedBy(3)
    }

    @Test fun `remove non-undoable osm quest does not trigger listener`() {
        questStatusListener.onRemoved(2L, QuestStatus.NEW)
        questStatusListener.onRemoved(3L, QuestStatus.INVISIBLE)
        questStatusListener.onRemoved(4L, QuestStatus.REVERT)
        verifyNothingHappened()
    }

    @Test fun `change osm quest to undoable triggers listener`() {
        questStatusListener.onChanged(osmQuest(1L, QuestStatus.ANSWERED), QuestStatus.NEW)
        questStatusListener.onChanged(osmQuest(2L, QuestStatus.HIDDEN), QuestStatus.NEW)
        questStatusListener.onChanged(osmQuest(3L, QuestStatus.CLOSED), QuestStatus.NEW)
        verifyIncreasedBy(3)
    }

    @Test fun `change osm quest from undoable triggers listener`() {
        questStatusListener.onChanged(osmQuest(1L, QuestStatus.REVERT), QuestStatus.ANSWERED)
        questStatusListener.onChanged(osmQuest(2L, QuestStatus.REVERT), QuestStatus.HIDDEN)
        questStatusListener.onChanged(osmQuest(3L, QuestStatus.REVERT), QuestStatus.CLOSED)
        verifyDecreasedBy(3)
    }

    @Test fun `change osm quest from non-undoable does not trigger listener`() {
        questStatusListener.onChanged(osmQuest(1L, QuestStatus.REVERT), QuestStatus.NEW)
        questStatusListener.onChanged(osmQuest(2L, QuestStatus.ANSWERED), QuestStatus.CLOSED)
        verifyNothingHappened()
    }

    @Test fun `update of osm quests triggers listener`() {
        on(osmQuestController.getAllUndoableCount()).thenReturn(20)
        questStatusListener.onUpdated(listOf(), listOf(), listOf())
        verify(listener).onUndoableOsmQuestsCountIncreased()
        assertEquals(20, source.count)
    }

    private fun verifyDecreasedBy(by: Int) {
        verify(listener, times(by)).onUndoableOsmQuestsCountDecreased()
        assertEquals(baseCount - by, source.count)
    }

    private fun verifyIncreasedBy(by: Int) {
        verify(listener, times(by)).onUndoableOsmQuestsCountIncreased()
        assertEquals(baseCount + by, source.count)
    }

    private fun verifyNothingHappened() {
        verifyZeroInteractions(listener)
        assertEquals(baseCount, source.count)
    }

    private fun osmQuest(id: Long, status: QuestStatus): OsmQuest {
        return OsmQuest(id, mock(), Element.Type.NODE, 1L, status, null, null, Date(), ElementPointGeometry(OsmLatLon(0.0,0.0)))
    }
}
