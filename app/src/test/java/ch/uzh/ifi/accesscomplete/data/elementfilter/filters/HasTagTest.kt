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

class HasTagTest {

    @Test fun matches() {
        val f = HasTag("highway", "residential")

        assertTrue(f.matches(mapOf("highway" to "residential")))
        assertFalse(f.matches(mapOf("highway" to "residental")))
        assertFalse(f.matches(mapOf("hipway" to "residential")))
        assertFalse(f.matches(mapOf()))
    }

    @Test fun toOverpassQLString() {
        assertEquals(
            "[highway = residential]",
            HasTag("highway", "residential").toOverpassQLString()
        )
        assertEquals(
            "['high:way' = residential]",
            HasTag("high:way", "residential").toOverpassQLString()
        )
        assertEquals(
            "[highway = 'resi:dential']",
            HasTag("highway", "resi:dential").toOverpassQLString()
        )
    }
}
