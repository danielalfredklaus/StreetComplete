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

package ch.uzh.ifi.accesscomplete.map

import dagger.Module
import dagger.Provides

@Module object MapModule {

    @Provides fun jawg(): VectorTileProvider = VectorTileProvider(
        "JawgMaps",
        "© JawgMaps",
        "https://www.jawg.io",
        "https://www.jawg.io/en/confidentiality/",
        "map_theme/jawg",
        String(byteArrayOf(
            109,76,57,88,52,83,119,120,102,115,65,71,102,111,106,118,71,105,105,111,110,57,104,80,
            75,117,71,76,75,120,80,98,111,103,76,121,77,98,116,97,107,65,50,103,74,51,88,56,56,103,
            99,86,108,84,83,81,55,79,68,54,79,102,98,90
        ))
    )
}
