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

import de.westnordost.countryboundaries.CountryBoundaries
import org.junit.Before
import org.junit.Test

import java.util.Date

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmNode
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.osmnotes.NotePositionsSource
import ch.uzh.ifi.accesscomplete.data.quest.*
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on

import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.verify
import java.util.concurrent.FutureTask

class OsmQuestGiverTest {

    private lateinit var notePositionsSource: NotePositionsSource
    private lateinit var osmQuestController: OsmQuestController
    private lateinit var questType: OsmElementQuestType<*>
    private lateinit var countryBoundaries: CountryBoundaries
    private lateinit var osmQuestGiver: OsmQuestGiver

    @Before fun setUp() {
        notePositionsSource = mock()
        on(notePositionsSource.getAllPositions(any())).thenReturn(emptyList())

        osmQuestController = mock()
        on(osmQuestController.getAllForElement(Element.Type.NODE, 1)).thenReturn(emptyList())
        on(osmQuestController.updateForElement(any(), any(), any(), any(), anyLong())).thenReturn(OsmQuestController.UpdateResult(0,0))

        questType = mock()
        on(questType.enabledInCountries).thenReturn(AllCountries)
        on(questType.isApplicableTo(NODE)).thenReturn(true)

        countryBoundaries = mock()
        val future = FutureTask { countryBoundaries }
        future.run()

        val questTypeRegistry: QuestTypeRegistry = mock()
        on(questTypeRegistry.all).thenReturn(listOf(questType))

        osmQuestGiver = OsmQuestGiver(notePositionsSource, osmQuestController, questTypeRegistry, future)
    }

    @Test fun `note blocks new quests`() {
        // there is a note at our position
        on(notePositionsSource.getAllPositions(any())).thenReturn(listOf(POS))

        osmQuestGiver.updateQuests(NODE, GEOM)

        verify(osmQuestController).updateForElement(emptyList(), emptyList(), GEOM, NODE.type, NODE.id)
    }

    @Test fun `previous quest blocks new quest`() {
        // there is a quest for the given element already
        val q = OsmQuest(questType, NODE.type, NODE.id, ElementPointGeometry(POS))
        on(osmQuestController.getAllForElement(NODE.type, NODE.id)).thenReturn(listOf(q))

        osmQuestGiver.updateQuests(NODE, GEOM)

        verify(osmQuestController).updateForElement(emptyList(), emptyList(), GEOM, NODE.type, NODE.id)
    }

    @Test fun `not applicable blocks new quest`() {
        // our quest type is not applicable to the element
        on(questType.isApplicableTo(NODE)).thenReturn(false)

        osmQuestGiver.updateQuests(NODE, GEOM)

        verify(osmQuestController).updateForElement(emptyList(), emptyList(), GEOM, NODE.type, NODE.id)
    }

    @Test fun `not applicable removes previous quest`() {
        // there is a quest for the given element already
        val q = OsmQuest(123L, questType, NODE.type, NODE.id, QuestStatus.NEW, null, null, Date(), ElementPointGeometry(POS))
        on(osmQuestController.getAllForElement(Element.Type.NODE, 1)).thenReturn(listOf(q))
        // but it is not applicable to the element anymore
        on(questType.isApplicableTo(NODE)).thenReturn(false)

        osmQuestGiver.updateQuests(NODE, GEOM)

        verify(osmQuestController).updateForElement(emptyList(), listOf(123L), GEOM, NODE.type, NODE.id)
    }

    @Test fun `applicable adds new quest`() {
        // there is no quest before, the quest is applicable etc. (code in setUp())
        osmQuestGiver.updateQuests(NODE, GEOM)

        val expectedQuest = OsmQuest(questType, NODE.type, NODE.id, GEOM)
        verify(osmQuestController).updateForElement(arrayListOf(expectedQuest), emptyList(), GEOM, NODE.type, NODE.id)
    }

    @Test fun `quest is only enabled in the country the element is in`() {
        on(questType.enabledInCountries).thenReturn(NoCountriesExcept("DE"))
        on(countryBoundaries.isInAny(anyDouble(), anyDouble(), any())).thenReturn(true)

        osmQuestGiver.updateQuests(NODE, GEOM)

        val expectedQuest = OsmQuest(questType, NODE.type, NODE.id, GEOM)
        verify(osmQuestController).updateForElement(arrayListOf(expectedQuest), emptyList(), GEOM, NODE.type, NODE.id)
    }

    @Test fun `quest is disabled in a country the element is not in`() {
        on(questType.enabledInCountries).thenReturn(AllCountriesExcept("DE"))
        on(countryBoundaries.isInAny(anyDouble(), anyDouble(), any())).thenReturn(true)

        osmQuestGiver.updateQuests(NODE, GEOM)

        verify(osmQuestController).updateForElement(emptyList(), emptyList(), GEOM, NODE.type, NODE.id)
    }

    @Test fun `recreate quests`() {
        val questType2: OsmElementQuestType<*> = mock()
        osmQuestGiver.recreateQuests(NODE, GEOM, listOf(questType, questType2))

        val expectedQuests = listOf(
            OsmQuest(questType, NODE.type, NODE.id, GEOM),
            OsmQuest(questType2, NODE.type, NODE.id, GEOM)
        )
        verify(osmQuestController).updateForElement(expectedQuests, emptyList(), GEOM, NODE.type, NODE.id)
    }
}

private val POS = OsmLatLon(10.0, 10.0)
private val NODE = OsmNode(1, 0, POS, null, null, null)
private val GEOM = ElementPointGeometry(POS)
