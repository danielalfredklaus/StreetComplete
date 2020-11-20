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

package ch.uzh.ifi.accesscomplete.data.upload

import org.junit.Assert.*
import org.junit.Test

class VersionIsBannedCheckerTest {
    @Test fun `banned version `() {
        assertEquals(IsBanned(null), VersionIsBannedChecker(URL, "StreetComplete 0.1").get())
    }

    @Test fun `not banned version `() {
        assertEquals(IsNotBanned, VersionIsBannedChecker(URL, "StreetComplete 3.0").get())
    }

    @Test fun `banned version with reason`() {
        assertEquals(
            IsBanned("This version does not correctly determine in which country you are, necessary to tag certain answers correctly."),
            VersionIsBannedChecker(URL, "StreetComplete 8.0").get()
        )
    }
}

private const val URL = "https://westnordost.de/streetcomplete/banned_versions.txt"
