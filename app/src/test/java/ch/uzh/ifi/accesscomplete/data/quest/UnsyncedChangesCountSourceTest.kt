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
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestDao
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNoteDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuest
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.invocation.InvocationOnMock
import java.util.*

class UnsyncedChangesCountSourceTest {
    private lateinit var osmQuestController: OsmQuestController
    private lateinit var osmNoteQuestController: OsmNoteQuestController
    private lateinit var createNoteDao: CreateNoteDao
    private lateinit var splitWayDao: OsmQuestSplitWayDao
    private lateinit var undoOsmQuestDao: UndoOsmQuestDao

    private lateinit var noteQuestStatusListener: OsmNoteQuestController.QuestStatusListener
    private lateinit var questStatusListener: OsmQuestController.QuestStatusListener
    private lateinit var createNoteListener: CreateNoteDao.Listener
    private lateinit var undoOsmQuestListener: UndoOsmQuestDao.Listener
    private lateinit var splitWayListener: OsmQuestSplitWayDao.Listener

    private lateinit var listener: UnsyncedChangesCountListener

    private lateinit var source: UnsyncedChangesCountSource

    private val baseCount = 1+2+3+4+5

    @Before fun setUp() {
        osmQuestController = mock()
        on(osmQuestController.addQuestStatusListener(any())).then { invocation: InvocationOnMock ->
            questStatusListener = invocation.arguments[0] as OsmQuestController.QuestStatusListener
            Unit
        }

        osmNoteQuestController = mock()
        on(osmNoteQuestController.addQuestStatusListener(any())).then { invocation: InvocationOnMock ->
            noteQuestStatusListener = invocation.arguments[0] as OsmNoteQuestController.QuestStatusListener
            Unit
        }

        createNoteDao = mock()
        on(createNoteDao.addListener(any())).then { invocation: InvocationOnMock ->
            createNoteListener = invocation.arguments[0] as CreateNoteDao.Listener
            Unit
        }

        splitWayDao = mock()
        on(splitWayDao.addListener(any())).then { invocation: InvocationOnMock ->
            splitWayListener = invocation.arguments[0] as OsmQuestSplitWayDao.Listener
            Unit
        }

        undoOsmQuestDao = mock()
        on(undoOsmQuestDao.addListener(any())).then { invocation: InvocationOnMock ->
            undoOsmQuestListener = invocation.arguments[0] as UndoOsmQuestDao.Listener
            Unit
        }

        on(osmQuestController.getAllAnsweredCount()).thenReturn(1)
        on(osmNoteQuestController.getAllAnsweredCount()).thenReturn(2)
        on(createNoteDao.getCount()).thenReturn(3)
        on(splitWayDao.getCount()).thenReturn(4)
        on(undoOsmQuestDao.getCount()).thenReturn(5)

        source = UnsyncedChangesCountSource(osmQuestController, osmNoteQuestController, createNoteDao, splitWayDao, undoOsmQuestDao)

        listener = mock()
        source.addListener(listener)
    }

    @Test fun count() {
        assertEquals(baseCount, source.count)
    }

    @Test fun `add undo quest triggers listener`() {
        undoOsmQuestListener.onAddedUndoOsmQuest()
        verifyIncreased()
    }

    @Test fun `remove undo quest triggers listener`() {
        undoOsmQuestListener.onDeletedUndoOsmQuest()
        verifyDecreased()
    }

    @Test fun `add split way triggers listener`() {
        splitWayListener.onAddedSplitWay()
        verifyIncreased()
    }

    @Test fun `remove split way triggers listener`() {
        splitWayListener.onDeletedSplitWay()
        verifyDecreased()
    }

    @Test fun `add create note triggers listener`() {
        createNoteListener.onAddedCreateNote()
        verifyIncreased()
    }

    @Test fun `remove create note triggers listener`() {
        createNoteListener.onDeletedCreateNote()
        verifyDecreased()
    }

    @Test fun `remove answered osm quest triggers listener`() {
        questStatusListener.onRemoved(1L, QuestStatus.ANSWERED)
        verifyDecreased()
    }

    @Test fun `remove non-answered osm quest does not trigger listener`() {
        questStatusListener.onRemoved(2L, QuestStatus.NEW)
        questStatusListener.onRemoved(3L, QuestStatus.INVISIBLE)
        questStatusListener.onRemoved(4L, QuestStatus.REVERT)
        questStatusListener.onRemoved(5L, QuestStatus.CLOSED)
        questStatusListener.onRemoved(6L, QuestStatus.HIDDEN)
        verifyNothingHappened()
    }

    @Test fun `change osm quest to answered triggers listener`() {
        questStatusListener.onChanged(osmQuest(1L, QuestStatus.ANSWERED), QuestStatus.NEW)
        verifyIncreased()
    }

    @Test fun `change osm quest from answered triggers listener`() {
        questStatusListener.onChanged(osmQuest(1L, QuestStatus.CLOSED), QuestStatus.ANSWERED)
        verifyDecreased()
    }

    @Test fun `change osm quest from non-answered does not trigger listener`() {
        questStatusListener.onChanged(osmQuest(1L, QuestStatus.REVERT), QuestStatus.CLOSED)
        verifyNothingHappened()
    }

    @Test fun `update of osm quests triggers listener`() {
        on(osmQuestController.getAllAnsweredCount()).thenReturn(101)
        questStatusListener.onUpdated(listOf(), listOf(), listOf())
        verify(listener).onUnsyncedChangesCountIncreased()
        assertEquals(baseCount + 100, source.count)
    }

    @Test fun `remove answered osm note quest triggers listener`() {
        noteQuestStatusListener.onRemoved(1L, QuestStatus.ANSWERED)
        verifyDecreased()
    }

    @Test fun `remove non-answered osm note quest does not trigger listener`() {
        noteQuestStatusListener.onRemoved(2L, QuestStatus.NEW)
        noteQuestStatusListener.onRemoved(3L, QuestStatus.INVISIBLE)
        noteQuestStatusListener.onRemoved(4L, QuestStatus.REVERT)
        noteQuestStatusListener.onRemoved(5L, QuestStatus.CLOSED)
        noteQuestStatusListener.onRemoved(6L, QuestStatus.HIDDEN)
        verifyNothingHappened()
    }

    @Test fun `change osm note quest to answered triggers listener`() {
        noteQuestStatusListener.onChanged(osmNoteQuest(1L, QuestStatus.ANSWERED), QuestStatus.NEW)
        verifyIncreased()
    }

    @Test fun `change osm note quest from answered triggers listener`() {
        noteQuestStatusListener.onChanged(osmNoteQuest(1L, QuestStatus.CLOSED), QuestStatus.ANSWERED)
        verifyDecreased()
    }

    @Test fun `change osm note quest from non-answered does not trigger listener`() {
        noteQuestStatusListener.onChanged(osmNoteQuest(1L, QuestStatus.REVERT), QuestStatus.CLOSED)
        verifyNothingHappened()
    }

    @Test fun `add answered osm note quest triggers listener`() {
        noteQuestStatusListener.onAdded(osmNoteQuest(1L, QuestStatus.ANSWERED))
        verifyIncreased()
    }

    @Test fun `add non-answered osm note quest does not trigger listener`() {
        noteQuestStatusListener.onAdded(osmNoteQuest(1L, QuestStatus.NEW))
        verifyNothingHappened()
    }

    @Test fun `update of osm note quests triggers listener`() {
        on(osmNoteQuestController.getAllAnsweredCount()).thenReturn(102)
        noteQuestStatusListener.onUpdated(listOf(), listOf(), listOf())
        verify(listener).onUnsyncedChangesCountIncreased()
        assertEquals(baseCount + 100, source.count)
    }

    private fun verifyDecreased() {
        verify(listener).onUnsyncedChangesCountDecreased()
        assertEquals(baseCount - 1, source.count)
    }

    private fun verifyIncreased() {
        verify(listener).onUnsyncedChangesCountIncreased()
        assertEquals(baseCount + 1, source.count)
    }

    private fun verifyNothingHappened() {
        verifyZeroInteractions(listener)
        assertEquals(baseCount, source.count)
    }

    private fun osmQuest(id: Long, status: QuestStatus): OsmQuest {
        return OsmQuest(id, mock(), Element.Type.NODE, 1L, status, null, null, Date(), ElementPointGeometry(OsmLatLon(0.0,0.0)))
    }

    private fun osmNoteQuest(id: Long, status: QuestStatus): OsmNoteQuest {
        return OsmNoteQuest(id, mock(), status, "", Date(), OsmNoteQuestType(), null)
    }
}
