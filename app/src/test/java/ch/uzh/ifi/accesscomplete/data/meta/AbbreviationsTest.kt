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

package ch.uzh.ifi.accesscomplete.data.meta

import org.junit.Test

import java.io.ByteArrayInputStream
import java.util.Locale

import org.junit.Assert.*

class AbbreviationsTest {
    @Test fun `capitalizes first letter`() {
        assertEquals("Straße", abbr("str: straße", Locale.GERMANY).getExpansion("str", true, true))
    }

    @Test fun `removes abbreviation dot`() {
        assertEquals("Straße", abbr("str: straße", Locale.GERMANY).getExpansion("str.", true, true))
    }

    @Test fun `ignores case`() {
        assertEquals("Straße", abbr("sTr: Straße", Locale.GERMANY).getExpansion("StR", true, true))
    }

    @Test fun `expects own word by default`() {
        assertNull(abbr("st: street").getExpansion("Hanswurst", true, true))
    }

    @Test fun `concatenable expansion`() {
        assertEquals("Konigstraat", abbr("...str: Straat").getExpansion("Konigstr", true, true))
    }

    @Test fun `concatenable exoabsuib on end`() {
        assertEquals("Konigstraat", abbr("...str$: Straat").getExpansion("Konigstr", true, true))
    }

    @Test fun `concatenable works normally for non-concatenation`() {
        assertEquals("Straat", abbr("...str: Straat").getExpansion("str", true, true))
    }

    @Test fun `get expansion of only first word`() {
        val abbr = abbr("^st: Saint")
        assertNull(abbr.getExpansion("st.", false, false))
        assertEquals("Saint", abbr.getExpansion("st.", true, false))
    }

    @Test fun `get expansion of only last word`() {
        val abbr = abbr("str$: Straße")
        assertNull(abbr.getExpansion("str", true, false))
        assertNull(abbr.getExpansion("str", true, true))
        assertEquals("Straße", abbr.getExpansion("str", false, true))
    }

    @Test fun `uses unicode`() {
        assertEquals("Блок", abbr("бл: Блок", Locale("ru", "RU")).getExpansion("бл", true, true))
    }

    @Test fun `locale dependent case`() {
        assertEquals("Блок", abbr("бл: блок", Locale("ru", "RU")).getExpansion("Бл", true, true))
    }

    @Test fun `finds abbreviation`() {
        assertFalse(abbr("str: Straße", Locale.GERMANY).containsAbbreviations("stri stra straße"))
        assertTrue(abbr("str: Straße", Locale.GERMANY).containsAbbreviations("stri str straße"))
    }

    @Test(expected = RuntimeException::class)
    fun `throws exception on invalid input`() {
        abbr("d:\n  - a\n  b: c\n")
    }

    private fun abbr(input: String, locale: Locale = Locale.US) =
        Abbreviations(ByteArrayInputStream(input.toByteArray(charset("UTF-8"))), locale)
}
