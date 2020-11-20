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

package ch.uzh.ifi.accesscomplete.data.download.tiles

object DownloadedTilesTable {
    const val NAME = "downloaded_tiles"

    object Columns {
        const val X = "x"
        const val Y = "y"
        const val TYPE = "quest_type"
        const val DATE = "date"
    }

    const val CREATE = """
        CREATE TABLE $NAME (
            ${Columns.X} int NOT NULL,
            ${Columns.Y} int NOT NULL,
            ${Columns.TYPE} varchar(255) NOT NULL,
            ${Columns.DATE} int NOT NULL,
            CONSTRAINT primary_key PRIMARY KEY (${Columns.X}, ${Columns.Y}, ${Columns.TYPE})
        );"""
}
