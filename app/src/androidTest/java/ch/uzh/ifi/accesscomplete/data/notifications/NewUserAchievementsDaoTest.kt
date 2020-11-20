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

package ch.uzh.ifi.accesscomplete.data.notifications

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class NewUserAchievementsDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: NewUserAchievementsDao

    @Before fun createDao() {
        dao = NewUserAchievementsDao(dbHelper)
    }

    @Test fun addPopFirst() {
        val listener: NewUserAchievementsDao.UpdateListener = mock(NewUserAchievementsDao.UpdateListener::class.java)
        dao.addListener(listener)
        dao.push(TWO to 2)
        dao.push(ONE to 1)
        dao.push(TWO to 1)
        dao.push(ONE to 8)

        assertEquals(ONE to 1, dao.pop())
        assertEquals(ONE to 8, dao.pop())
        assertEquals(TWO to 1, dao.pop())
        assertEquals(TWO to 2, dao.pop())
        assertEquals(null, dao.pop())

        verify(listener, times(8)).onNewUserAchievementsUpdated()
    }

    @Test fun addPop() {
        val listener: NewUserAchievementsDao.UpdateListener = mock(NewUserAchievementsDao.UpdateListener::class.java)
        dao.addListener(listener)

        assertEquals(0, dao.getCount())

        dao.push(ONE to 4)
        assertEquals(1, dao.getCount())
        verify(listener, times(1)).onNewUserAchievementsUpdated()

        dao.push(ONE to 4)
        assertEquals(1, dao.getCount())
        verify(listener, times(1)).onNewUserAchievementsUpdated()

        dao.push(ONE to 1)
        assertEquals(2, dao.getCount())
        verify(listener, times(2)).onNewUserAchievementsUpdated()

        dao.pop()
        assertEquals(1, dao.getCount())
        verify(listener, times(3)).onNewUserAchievementsUpdated()

        dao.pop()
        assertEquals(0, dao.getCount())
        verify(listener, times(4)).onNewUserAchievementsUpdated()
    }
}

private const val ONE = "one"
private const val TWO = "two"
