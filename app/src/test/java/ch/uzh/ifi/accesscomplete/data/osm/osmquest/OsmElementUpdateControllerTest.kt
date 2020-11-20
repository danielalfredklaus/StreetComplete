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

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmNode
import ch.uzh.ifi.accesscomplete.data.MapDataApi
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometryCreator
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.MergedElementDao
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class OsmElementUpdateControllerTest {

    private lateinit var mapDataApi: MapDataApi
    private lateinit var elementGeometryCreator: ElementGeometryCreator
    private lateinit var elementDB: MergedElementDao
    private lateinit var questGiver: OsmQuestGiver
    private lateinit var c: OsmElementUpdateController

    @Before fun setUp() {
        mapDataApi = mock()
        elementGeometryCreator = mock()
        elementDB = mock()
        questGiver = mock()
        c = OsmElementUpdateController(mapDataApi, elementGeometryCreator, elementDB, questGiver)
    }

    @Test fun delete() {
        c.delete(Element.Type.NODE, 123L)

        verify(elementDB).delete(Element.Type.NODE, 123L)
        verify(questGiver).deleteQuests(Element.Type.NODE, 123L)
    }

    @Test fun update() {
        val element = OsmNode(123L, 1, 0.0, 0.0, null)
        val point = ElementPointGeometry(element.position)

        on(elementGeometryCreator.create(element)).thenReturn(point)

        c.update(element, null)

        verify(elementDB).put(element)
        verify(elementGeometryCreator).create(element)
        verify(questGiver).updateQuests(element, point)
    }

    @Test fun `update deleted`() {
        val element = OsmNode(123L, 1, 0.0, 0.0, null)

        on(elementGeometryCreator.create(element)).thenReturn(null)

        c.update(element, null)

        verify(elementDB).delete(element.type, element.id)
        verify(questGiver).deleteQuests(element.type, element.id)
    }

    @Test fun recreate() {
        val element = OsmNode(123L, 1, 0.0, 0.0, null)
        val point = ElementPointGeometry(element.position)
        val questType: OsmElementQuestType<Boolean> = mock()
        val questTypes = listOf(questType)
        on(elementGeometryCreator.create(element)).thenReturn(point)

        c.update(element, questTypes)


        verify(elementDB).put(element)
        verify(elementGeometryCreator).create(element)
        verify(questGiver).recreateQuests(element, point, questTypes)
    }
}
