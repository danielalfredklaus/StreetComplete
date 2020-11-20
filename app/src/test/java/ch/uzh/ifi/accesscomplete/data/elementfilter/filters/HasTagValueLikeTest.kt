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

package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import ch.uzh.ifi.accesscomplete.data.elementfilter.matches
import org.junit.Assert.*
import org.junit.Test

class HasTagValueLikeTest {

    @Test fun `matches like dot`() {
        val f = HasTagValueLike("highway",".esidential")

        assertTrue(f.matches(mapOf("highway" to "residential")))
        assertTrue(f.matches(mapOf("highway" to "wesidential")))
        assertFalse(f.matches(mapOf("highway" to "rresidential")))
        assertFalse(f.matches(mapOf()))
    }

    @Test fun `matches like or`() {
        val f = HasTagValueLike("highway", "residential|unclassified")

        assertTrue(f.matches(mapOf("highway" to "residential")))
        assertTrue(f.matches(mapOf("highway" to "unclassified")))
        assertFalse(f.matches(mapOf("highway" to "blub")))
        assertFalse(f.matches(mapOf()))
    }

    @Test fun `groups values properly`() {
        val f = HasTagValueLike("highway", "residential|unclassified")

        assertEquals(
            "[highway ~ '^(residential|unclassified)$']",
            f.toOverpassQLString()
        )
    }

    @Test fun `key value to string`() {
        val f = HasTagValueLike("highway",".*")
        assertEquals(
            "[highway ~ '^(.*)$']",
            f.toOverpassQLString()
        )
    }
}
