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

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryAdd
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryDelete
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryModify
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.*
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.ElementKey
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.OsmLatLon
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class OsmQuestDaoTest : ApplicationDbTestCase() {
    private lateinit var geometryDao: ElementGeometryDao
    private lateinit var dao: OsmQuestDao

    @Before fun createDaos() {
        val elementGeometryMapping = ElementGeometryMapping(serializer)
        geometryDao = ElementGeometryDao(dbHelper, elementGeometryMapping)
        val list = listOf<QuestType<*>>(TEST_QUEST_TYPE, TEST_QUEST_TYPE2)
        val mapping = OsmQuestMapping(serializer, QuestTypeRegistry(list), elementGeometryMapping)
        dao = OsmQuestDao(dbHelper, mapping)
    }

    @Test fun addGetNoChanges() {
        val quest = create()
        addToDaos(quest)
        assertEquals(quest, dao.get(quest.id!!))
    }

    @Test fun addGetWithChanges() {
        val quest = create(
                changes = StringMapChanges(listOf(
                        StringMapEntryAdd("a key", "a value"),
                        StringMapEntryDelete("delete this", "key"),
                        StringMapEntryModify("modify", "this", "to that")
                )),
                changesSource = "bla",
                status = QuestStatus.CLOSED
        )
        addToDaos(quest)

        assertEquals(quest, dao.get(quest.id!!))
    }

    @Test fun deleteButNothingIsThere() {
        assertFalse(dao.delete(1L))
    }

    @Test fun addAndDelete() {
        val quest = create()
        addToDaos(quest)

        assertTrue(dao.delete(quest.id!!))
        assertNull(dao.get(quest.id!!))
        assertFalse(dao.delete(quest.id!!))
    }

    @Test fun update() {
        val quest = create()
        addToDaos(quest)

        quest.status = QuestStatus.HIDDEN
        quest.changesSource = "ho"
        quest.changes = StringMapChanges(listOf(StringMapEntryAdd("a key", "a value")))

        dao.update(quest)

        assertEquals(quest, dao.get(quest.id!!))
    }

    @Test fun updateAll() {
        val quests = listOf(
            create(elementId = 1),
            create(elementId = 2),
            create(elementId = 3)
        )
        addToDaos(*quests.toTypedArray())

        quests.forEach { it.status = QuestStatus.HIDDEN }

        assertEquals(3, dao.updateAll(quests))

        assertEquals(dao.getAll(), quests)
    }

    @Test fun addAllAndDeleteAll() {
        val quests = listOf(
                create(elementId = 1),
                create(elementId = 2),
                create(elementId = 3)
        )
        geometryDao.putAll(quests.map { it.geometryEntry } )
        assertEquals(3, dao.addAll(quests))

        for (quest in quests) {
            assertNotNull(quest.id)
            assertEquals(quest, dao.get(quest.id!!))
        }

        assertEquals(3, dao.deleteAllIds(quests.map { it.id!! }))
        assertEquals(0, dao.getCount())
    }

    @Test fun getAllByBBox() {
        addToDaos(
                create(elementId = 1, geometry = ElementPointGeometry(OsmLatLon(5.0, 5.0))),
                create(elementId = 2, geometry = ElementPointGeometry(OsmLatLon(11.0, 11.0)))
        )

        assertEquals(1, dao.getAll(bounds = BoundingBox(0.0, 0.0, 10.0, 10.0)).size)
        assertEquals(2, dao.getAll().size)
    }

    @Test fun getAllByStatus() {
        addToDaos(
                create(elementId = 1, status = QuestStatus.HIDDEN),
                create(elementId = 2, status = QuestStatus.NEW)
        )

        assertEquals(1, dao.getAll(statusIn = listOf(QuestStatus.HIDDEN)).size)
        assertEquals(1, dao.getAll(statusIn = listOf(QuestStatus.NEW)).size)
        assertEquals(0, dao.getAll(statusIn = listOf(QuestStatus.CLOSED)).size)
        assertEquals(2, dao.getAll(statusIn = listOf(QuestStatus.NEW, QuestStatus.HIDDEN)).size)
    }

    @Test fun getAllByElement() {
        addToDaos(
                create(elementType = Element.Type.NODE, elementId = 1),
                create(elementType = Element.Type.WAY, elementId = 2)
        )

        assertEquals(1, dao.getAll(element = ElementKey(Element.Type.WAY, 2)).size)
        assertEquals(1, dao.getAll(element = ElementKey(Element.Type.NODE, 1)).size)
    }

    @Test fun getAllByQuestTypes() {
        addToDaos(
                create(questType = TEST_QUEST_TYPE),
                create(questType = TEST_QUEST_TYPE2)
        )

        assertEquals(1, dao.getAll(questTypes = listOf(TestQuestType::class.java.simpleName)).size)
        assertEquals(2, dao.getAll(questTypes = listOf(TestQuestType::class.java.simpleName, TestQuestType2::class.java.simpleName)).size)
        assertEquals(1, dao.getAll(questTypes = listOf(TestQuestType::class.java.simpleName)).size)
    }

    @Test fun getAllIds() {
        addToDaos(
                create(questType = TEST_QUEST_TYPE),
                create(questType = TEST_QUEST_TYPE2)
        )
        assertEquals(2, dao.getAllIds().size)
    }

    @Test fun getCount() {
        addToDaos(
                create(questType = TEST_QUEST_TYPE),
                create(questType = TEST_QUEST_TYPE2)
        )
        assertEquals(2, dao.getCount())
    }

    @Test fun deleteAll() {
        addToDaos(
                create(questType = TEST_QUEST_TYPE),
                create(questType = TEST_QUEST_TYPE2)
        )

        assertEquals(2, dao.deleteAll())
    }

    @Test fun deleteAllTriggersListenerIfAtLeastOneIsOfStatusAnswered() {
        addToDaos(
            create(questType = TEST_QUEST_TYPE),
            create(questType = TEST_QUEST_TYPE2, status = QuestStatus.ANSWERED)
        )

        dao.deleteAll()
    }

    private fun addToDaos(vararg quests: OsmQuest) {
        for (quest in quests) {
            geometryDao.put(quest.geometryEntry)
            assertTrue(dao.add(quest))
        }
    }
}

private val OsmQuest.geometryEntry get() = ElementGeometryEntry(elementType, elementId, geometry)

private val TEST_QUEST_TYPE = TestQuestType()
private val TEST_QUEST_TYPE2 = TestQuestType2()

private fun create(
    questType: OsmElementQuestType<*> = TEST_QUEST_TYPE,
    elementType: Element.Type = Element.Type.NODE,
    elementId: Long = 1,
    status: QuestStatus = QuestStatus.NEW,
    geometry: ElementGeometry = ElementPointGeometry(OsmLatLon(5.0, 5.0)),
    changes: StringMapChanges? = null,
    changesSource: String? = null
) = OsmQuest(
        null, questType, elementType, elementId, status, changes, changesSource, Date(), geometry
)
