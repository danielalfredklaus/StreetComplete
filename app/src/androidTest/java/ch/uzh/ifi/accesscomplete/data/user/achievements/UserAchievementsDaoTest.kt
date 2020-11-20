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

package ch.uzh.ifi.accesscomplete.data.user.achievements

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserAchievementsDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: UserAchievementsDao

    @Before fun createDao() {
        dao = UserAchievementsDao(dbHelper)
    }

    @Test fun putGetAll() {
        dao.put(ONE, 1)
        dao.put(ONE, 4)
        dao.put(TWO, 2)
        assertEquals(mapOf(
            ONE to 4,
            TWO to 2
        ),dao.getAll())
    }
}

private const val ONE = "one"
private const val TWO = "two"
