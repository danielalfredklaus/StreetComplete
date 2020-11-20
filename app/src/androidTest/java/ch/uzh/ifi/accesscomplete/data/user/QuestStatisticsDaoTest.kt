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

package ch.uzh.ifi.accesscomplete.data.user

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class QuestStatisticsDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: QuestStatisticsDao

    @Before fun createDao() {
        dao = QuestStatisticsDao(dbHelper)
    }

    @Test fun getZero() {
        assertEquals(0, dao.getAmount(ONE))
    }

    @Test fun getOne() {
        val listener = mock(QuestStatisticsDao.Listener::class.java)
        dao.addListener(listener)
        dao.addOne(ONE)
        assertEquals(1, dao.getAmount(ONE))
        verify(listener).onAddedOne(ONE)
    }

    @Test fun getTwo() {
        val listener = mock(QuestStatisticsDao.Listener::class.java)
        dao.addListener(listener)
        dao.addOne(ONE)
        dao.addOne(ONE)
        assertEquals(2, dao.getAmount(ONE))
        verify(listener, times(2)).onAddedOne(ONE)
    }

    @Test fun getTotal() {
        dao.addOne(ONE)
        dao.addOne(ONE)
        dao.addOne(TWO)
        assertEquals(3, dao.getTotalAmount())
    }

    @Test fun subtract() {
        val listener = mock(QuestStatisticsDao.Listener::class.java)
        dao.addListener(listener)
        dao.addOne(ONE)
        verify(listener).onAddedOne(ONE)
        dao.subtractOne(ONE)
        verify(listener).onSubtractedOne(ONE)
        assertEquals(0, dao.getAmount(ONE))
    }

    @Test fun getAmountOfSeveral() {
        dao.addOne(ONE)
        dao.addOne(ONE)
        dao.addOne(TWO)
        dao.addOne(THREE)
        assertEquals(3, dao.getAmount(listOf(ONE, TWO)))
    }

    @Test fun replaceAll() {
        dao.addOne(ONE)
        dao.addOne(TWO)
        val listener = mock(QuestStatisticsDao.Listener::class.java)
        dao.addListener(listener)
        dao.replaceAll(mapOf(
                ONE to 4,
                THREE to 1
        ))
        verify(listener).onReplacedAll()
        assertEquals(4, dao.getAmount(ONE))
        assertEquals(0, dao.getAmount(TWO))
        assertEquals(1, dao.getAmount(THREE))
    }

    @Test fun getAll() {
        dao.addOne(ONE)
        dao.addOne(ONE)
        dao.addOne(TWO)
        assertEquals(mapOf(
            ONE to 2,
            TWO to 1
        ),dao.getAll())
    }
}

private const val ONE = "one"
private const val TWO = "two"
private const val THREE = "three"
