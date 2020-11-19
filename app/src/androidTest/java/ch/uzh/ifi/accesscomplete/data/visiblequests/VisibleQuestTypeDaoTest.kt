package ch.uzh.ifi.accesscomplete.data.visiblequests

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.DisabledTestQuestType
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.TestQuestType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VisibleQuestTypeDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: VisibleQuestTypeDao

    private val testQuestType = TestQuestType()
    private val disabledTestQuestType = DisabledTestQuestType()

    @Before fun createDao() {
        dao = VisibleQuestTypeDao(dbHelper)
    }

    @Test fun defaultEnabledQuest() {
        assertTrue(dao.isVisible(testQuestType))
    }

    @Test fun defaultDisabledQuests() {
        assertFalse(dao.isVisible(disabledTestQuestType))
    }

    @Test fun disableQuest() {
        dao.setVisible(testQuestType, false)
        assertFalse(dao.isVisible(testQuestType))
    }

    @Test fun enableQuest() {
        dao.setVisible(disabledTestQuestType, true)
        assertTrue(dao.isVisible(disabledTestQuestType))
    }

    @Test fun reset() {
        dao.setVisible(testQuestType, false)
        assertFalse(dao.isVisible(testQuestType))
        dao.clear()
        assertTrue(dao.isVisible(testQuestType))
    }
}
