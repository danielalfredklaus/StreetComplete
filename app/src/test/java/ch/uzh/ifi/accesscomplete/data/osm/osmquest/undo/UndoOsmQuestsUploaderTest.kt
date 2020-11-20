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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmNode
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryAdd
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementUpdateController
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.SingleOsmElementTagChangesUploader
import ch.uzh.ifi.accesscomplete.data.osm.upload.ChangesetConflictException
import ch.uzh.ifi.accesscomplete.data.osm.upload.ElementConflictException
import ch.uzh.ifi.accesscomplete.data.osm.upload.ElementDeletedException
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenQuestChangesetsManager
import ch.uzh.ifi.accesscomplete.data.user.StatisticsUpdater
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.*
import java.util.concurrent.atomic.AtomicBoolean

class UndoOsmQuestsUploaderTest {
    private lateinit var undoQuestDB: UndoOsmQuestDao
    private lateinit var changesetManager: OpenQuestChangesetsManager
    private lateinit var singleChangeUploader: SingleOsmElementTagChangesUploader
    private lateinit var statisticsUpdater: StatisticsUpdater
    private lateinit var elementUpdateController: OsmElementUpdateController
    private lateinit var uploader: UndoOsmQuestsUploader

    @Before fun setUp() {
        undoQuestDB = mock()
        changesetManager = mock()
        singleChangeUploader = mock()
        statisticsUpdater = mock()
        elementUpdateController = mock()
        uploader = UndoOsmQuestsUploader(changesetManager, elementUpdateController,
            undoQuestDB, singleChangeUploader, statisticsUpdater)
    }

    @Test fun `cancel upload works`() {
        val cancelled = AtomicBoolean(true)
        uploader.upload(cancelled)
        verifyZeroInteractions(changesetManager, singleChangeUploader, statisticsUpdater, elementUpdateController, undoQuestDB)
    }

    @Test fun `catches ElementConflict exception`() {
        on(undoQuestDB.getAll()).thenReturn(listOf(createUndoQuest()))
        on(singleChangeUploader.upload(anyLong(), any(), any()))
            .thenThrow(ElementConflictException())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(mock())

        uploader.upload(AtomicBoolean(false))

        // will not throw ElementConflictException
    }

    @Test fun `discard if element was deleted`() {
        val q = createUndoQuest()
        on(undoQuestDB.getAll()).thenReturn(listOf(q))
        on(singleChangeUploader.upload(anyLong(), any(), any()))
            .thenThrow(ElementDeletedException())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(mock())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(uploader.uploadedChangeListener)?.onDiscarded(q.osmElementQuestType.javaClass.simpleName, q.position)
        verify(elementUpdateController).delete(any(), anyLong())
    }

    @Test fun `catches ChangesetConflictException exception and tries again once`() {
        on(undoQuestDB.getAll()).thenReturn(listOf(createUndoQuest()))
        on(singleChangeUploader.upload(anyLong(), any(), any()))
            .thenThrow(ChangesetConflictException())
            .thenReturn(createElement())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(mock())

        uploader.upload(AtomicBoolean(false))

        // will not throw ChangesetConflictException but instead call single upload twice
        verify(changesetManager).getOrCreateChangeset(any(), any())
        verify(changesetManager).createChangeset(any(), any())
        verify(singleChangeUploader, times(2)).upload(anyLong(), any(), any())
    }

    @Test fun `delete each uploaded quest from local DB and calls listener`() {
        val quests = listOf(createUndoQuest(), createUndoQuest())

        on(undoQuestDB.getAll()).thenReturn(quests)
        on(singleChangeUploader.upload(anyLong(), any(), any()))
            .thenThrow(ElementConflictException())
            .thenReturn(createElement())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(mock())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(undoQuestDB, times(2)).delete(anyLong())
        verify(uploader.uploadedChangeListener)?.onUploaded(quests[0].osmElementQuestType.javaClass.simpleName, quests[0].position)
        verify(uploader.uploadedChangeListener)?.onDiscarded(quests[1].osmElementQuestType.javaClass.simpleName, quests[1].position)

        verify(elementUpdateController, times(1)).update(any(), isNull())
        verify(elementUpdateController, times(2)).get(any(), anyLong())
        verify(statisticsUpdater).subtractOne(any(), any())
        verifyNoMoreInteractions(elementUpdateController)
    }

    @Test fun `delete unreferenced elements and clean metadata at the end`() {
        val quest = createUndoQuest()

        on(undoQuestDB.getAll()).thenReturn(listOf(quest))
        on(singleChangeUploader.upload(anyLong(), any(), any())).thenReturn(createElement())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(mock())

        uploader.upload(AtomicBoolean(false))

        verify(quest.osmElementQuestType).cleanMetadata()
    }
}

private fun createUndoQuest() : UndoOsmQuest {
    val changes = StringMapChanges(listOf(StringMapEntryAdd("surface","asphalt")))
    val geometry = ElementPointGeometry(OsmLatLon(0.0, 0.0))
    return UndoOsmQuest(1, mock(), Element.Type.NODE, 1, changes, "survey", geometry)
}

private fun createElement() = OsmNode(1,1,OsmLatLon(0.0,0.0),null)
