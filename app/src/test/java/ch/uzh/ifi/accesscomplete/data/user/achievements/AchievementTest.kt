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

import org.junit.Assert.*
import org.junit.Test

class AchievementTest {
    @Test fun `getPointThreshold for level 0 is 0`() {
        assertEquals(0, achievement { 100 }.getPointThreshold(0))
    }

    @Test fun `getPointThreshold with linear progression`() {
        assertEquals(100, achievement { 10 }.getPointThreshold(10))
    }

    @Test fun `getPointThreshold with other progression`() {
        val a = achievement { it+1 }
        assertEquals(1, a.getPointThreshold(1))
        assertEquals(3, a.getPointThreshold(2))
        assertEquals(6, a.getPointThreshold(3))
        assertEquals(10, a.getPointThreshold(4))
        assertEquals(15, a.getPointThreshold(5))
        assertEquals(21, a.getPointThreshold(6))
        assertEquals(28, a.getPointThreshold(7))
        assertEquals(36, a.getPointThreshold(8))
    }

    private fun achievement(func: (Int) -> Int): Achievement =
        Achievement("abc", 0, 0, 0, DaysActive, func, mapOf(), -1)
}
