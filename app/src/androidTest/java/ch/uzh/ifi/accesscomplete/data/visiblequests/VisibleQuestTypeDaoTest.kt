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
