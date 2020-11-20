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

import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmWay
import ch.uzh.ifi.accesscomplete.on
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementUpdateController
import ch.uzh.ifi.accesscomplete.data.osm.upload.ChangesetConflictException
import ch.uzh.ifi.accesscomplete.data.osm.upload.ElementConflictException
import ch.uzh.ifi.accesscomplete.data.osm.upload.ElementDeletedException
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenQuestChangesetsManager
import ch.uzh.ifi.accesscomplete.data.user.StatisticsUpdater
import ch.uzh.ifi.accesscomplete.mock
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.atomic.AtomicBoolean

class SplitWaysUploaderTest {
    private lateinit var splitWayDB: OsmQuestSplitWayDao
    private lateinit var changesetManager: OpenQuestChangesetsManager
    private lateinit var splitSingleOsmWayUploader: SplitSingleWayUploader
    private lateinit var statisticsUpdater: StatisticsUpdater
    private lateinit var elementUpdateController: OsmElementUpdateController
    private lateinit var uploader: SplitWaysUploader

    @Before fun setUp() {
        splitWayDB = mock()
        changesetManager = mock()
        splitSingleOsmWayUploader = mock()
        elementUpdateController = mock()
        statisticsUpdater = mock()
        uploader = SplitWaysUploader(changesetManager, elementUpdateController, splitWayDB,
            splitSingleOsmWayUploader, statisticsUpdater)
    }

    @Test fun `cancel upload works`() {
        val cancelled = AtomicBoolean(true)
        uploader.upload(cancelled)
        verifyZeroInteractions(changesetManager, splitSingleOsmWayUploader, elementUpdateController, statisticsUpdater, splitWayDB)
    }

    @Test fun `catches ElementConflict exception`() {
        on(splitWayDB.getAll()).thenReturn(listOf(createOsmSplitWay()))
        on(splitSingleOsmWayUploader.upload(anyLong(), any(), anyList()))
            .thenThrow(ElementConflictException())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(createElement())

        uploader.upload(AtomicBoolean(false))

        // will not throw ElementConflictException
    }

    @Test fun `discard if element was deleted`() {
        val q = createOsmSplitWay()
        on(splitWayDB.getAll()).thenReturn(listOf(q))
        on(splitSingleOsmWayUploader.upload(anyLong(), any(), any()))
            .thenThrow(ElementDeletedException())
        on(elementUpdateController.get(any(), anyLong())).thenReturn(createElement())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(uploader.uploadedChangeListener)?.onDiscarded(q.questType.javaClass.simpleName, q.position)
    }

    @Test fun `catches ChangesetConflictException exception and tries again once`() {
        on(splitWayDB.getAll()).thenReturn(listOf(createOsmSplitWay()))
        on(splitSingleOsmWayUploader.upload(anyLong(), any(), anyList()))
            .thenThrow(ChangesetConflictException())
            .thenReturn(listOf(createElement()))
        on(elementUpdateController.get(any(), anyLong())).thenReturn(createElement())

        uploader.upload(AtomicBoolean(false))

        // will not throw ChangesetConflictException but instead call single upload twice
        verify(changesetManager).getOrCreateChangeset(any(), any())
        verify(changesetManager).createChangeset(any(), any())
        verify(splitSingleOsmWayUploader, times(2)).upload(anyLong(), any(), anyList())
    }

    @Test fun `delete each uploaded split from local DB and calls listener`() {
        val quests = listOf(createOsmSplitWay(), createOsmSplitWay())

        on(splitWayDB.getAll()).thenReturn(quests)
        on(splitSingleOsmWayUploader.upload(anyLong(), any(), anyList()))
            .thenThrow(ElementConflictException())
            .thenReturn(listOf(createElement()))
        on(elementUpdateController.get(any(), anyLong())).thenReturn(createElement())

        uploader.uploadedChangeListener = mock()
        uploader.upload(AtomicBoolean(false))

        verify(splitWayDB, times(2)).delete(anyLong())
        verify(uploader.uploadedChangeListener)?.onUploaded(quests[0].questType.javaClass.simpleName, quests[0].position)
        verify(uploader.uploadedChangeListener)?.onDiscarded(quests[1].questType.javaClass.simpleName,quests[1].position)

        verify(elementUpdateController, times(1)).update(any(), any())
        verify(elementUpdateController, times(2)).get(any(), anyLong())
        verify(statisticsUpdater).addOne(any(), any())
        verifyNoMoreInteractions(elementUpdateController)
    }

    @Test fun `delete unreferenced elements and clean metadata at the end`() {
        val quest = createOsmSplitWay()

        on(splitWayDB.getAll()).thenReturn(listOf(quest))
        on(splitSingleOsmWayUploader.upload(anyLong(), any(), any()))
            .thenReturn(listOf(createElement()))
        on(elementUpdateController.get(any(), anyLong())).thenReturn(createElement())

        uploader.upload(AtomicBoolean(false))

        verify(quest.osmElementQuestType).cleanMetadata()
    }
}

private fun createOsmSplitWay() = OsmQuestSplitWay(
    1,
    mock(),
    1,
    "survey",
    listOf(SplitAtPoint(OsmLatLon(1.0,0.1))),
    listOf(mock(), mock()))

private fun createElement() = OsmWay(1,1, listOf(1,2,3), null)
