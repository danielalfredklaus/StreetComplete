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

package ch.uzh.ifi.accesscomplete.data.osmnotes.notequests

import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable

object OsmNoteQuestTable {
    const val NAME = "osm_notequests"

    const val NAME_MERGED_VIEW = "osm_notequests_full"

    object Columns {
        const val QUEST_ID = "quest_id"
        const val NOTE_ID = NoteTable.Columns.ID
        const val QUEST_STATUS = "quest_status"
        const val COMMENT = "changes"
        const val LAST_UPDATE = "last_update"
        const val IMAGE_PATHS = "image_paths"
    }

    const val CREATE = """
        CREATE TABLE $NAME (
            ${Columns.QUEST_ID} INTEGER PRIMARY KEY,
            ${Columns.QUEST_STATUS} varchar(255) NOT NULL,
            ${Columns.COMMENT} text,
            ${Columns.LAST_UPDATE} int NOT NULL,
            ${Columns.NOTE_ID} INTEGER UNIQUE NOT NULL,
            ${Columns.IMAGE_PATHS} blob
            REFERENCES ${NoteTable.NAME}(${NoteTable.Columns.ID})
        );"""

    const val CREATE_VIEW = """
        CREATE VIEW $NAME_MERGED_VIEW AS
        SELECT * FROM $NAME
            INNER JOIN ${NoteTable.NAME}
            USING (${NoteTable.Columns.ID});"""
}
