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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.countryboundaries.CountryBoundaries
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmNode
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.MapDataApi
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometryCreator
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.MergedElementDao
import ch.uzh.ifi.accesscomplete.data.osmnotes.NotePositionsSource
import ch.uzh.ifi.accesscomplete.data.quest.AllCountries
import ch.uzh.ifi.accesscomplete.data.quest.Countries
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestAnswerFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.FutureTask
import javax.inject.Provider

class OsmApiQuestDownloaderTest {
    private lateinit var elementDb: MergedElementDao
    private lateinit var osmQuestController: OsmQuestController
    private lateinit var countryBoundaries: CountryBoundaries
    private lateinit var notePositionsSource: NotePositionsSource
    private lateinit var mapDataApi: MapDataApi
    private lateinit var mapDataWithGeometry: CachingMapDataWithGeometry
    private lateinit var elementGeometryCreator: ElementGeometryCreator
    private lateinit var downloader: OsmApiQuestDownloader

    private val bbox = BoundingBox(0.0, 0.0, 1.0, 1.0)

    @Before fun setUp() {
        elementDb = mock()
        osmQuestController = mock()
        on(osmQuestController.replaceInBBox(any(), any(), any())).thenReturn(OsmQuestController.UpdateResult(0,0))
        countryBoundaries = mock()
        mapDataApi = mock()
        mapDataWithGeometry = mock()
        elementGeometryCreator = mock()
        notePositionsSource = mock()
        val countryBoundariesFuture = FutureTask { countryBoundaries }
        countryBoundariesFuture.run()
        val mapDataProvider = Provider { mapDataWithGeometry }
        downloader = OsmApiQuestDownloader(
            elementDb, osmQuestController, countryBoundariesFuture, notePositionsSource, mapDataApi,
            mapDataProvider, elementGeometryCreator)
    }

    @Test fun `creates quest for element`() {
        val pos = OsmLatLon(1.0, 1.0)
        val node = OsmNode(5, 0, pos, null)
        val geom = ElementPointGeometry(pos)
        val questType = TestMapDataQuestType(listOf(node))

        on(mapDataWithGeometry.getNodeGeometry(5)).thenReturn(geom)
        on(osmQuestController.replaceInBBox(any(), any(), any())).thenAnswer {
            val createdQuests = it.arguments[0] as ConcurrentLinkedQueue<OsmQuest>
            assertEquals(1, createdQuests.size)
            val quest = createdQuests.first()
            assertEquals(5, quest.elementId)
            assertEquals(Element.Type.NODE, quest.elementType)
            assertEquals(geom, quest.geometry)
            assertEquals(questType, quest.osmElementQuestType)
            OsmQuestController.UpdateResult(1,0)
        }

        downloader.download(listOf(questType), bbox)

        verify(elementDb).putAll(any())
        verify(elementDb).deleteUnreferenced()
        verify(osmQuestController).replaceInBBox(any(), any(), any())
    }
}

private class TestMapDataQuestType(private val list: List<Element>) : OsmElementQuestType<String> {

    override var enabledInCountries: Countries = AllCountries

    override val icon = 0
    override val commitMessage = ""
    override fun getTitle(tags: Map<String, String>) = 0
    override fun createForm() = object : AbstractQuestAnswerFragment<String>() {}
    override fun isApplicableTo(element: Element) = false
    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {}
    override fun getApplicableElements(mapData: MapDataWithGeometry): Iterable<Element> = list
}
