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

import java.util.Date
import ch.uzh.ifi.accesscomplete.data.quest.Quest
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.notes.Note
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry

/** Represents one task for the user to contribute to a public OSM note */
data class OsmNoteQuest(
        override var id: Long?,
        val note: Note,
        override var status: QuestStatus,
        var comment: String?,
        override var lastUpdate: Date,
        private val questType: OsmNoteQuestType,
        var imagePaths: List<String>?
) : Quest {

    constructor(note: Note, osmNoteQuestType: OsmNoteQuestType)
        : this(null, note, QuestStatus.NEW, null, Date(), osmNoteQuestType, null)

    override val type: QuestType<*> get() = questType
    override val markerLocations: Collection<LatLon> get() = listOf(note.position)
    override val geometry: ElementGeometry get() = ElementPointGeometry(center)
    override val center: LatLon get() = note.position

    fun probablyContainsQuestion(): Boolean {
        /* from left to right (if smartass IntelliJ wouldn't mess up left-to-right):
           - latin question mark
           - greek question mark (a different character than semikolon, though same appearance)
           - semikolon (often used instead of proper greek question mark)
           - mirrored question mark (used in script written from right to left, like Arabic)
           - armenian question mark
           - ethopian question mark
           - full width question mark (often used in modern Chinese / Japanese)
           (Source: https://en.wikipedia.org/wiki/Question_mark)

            NOTE: some languages, like Thai, do not use any question mark, so this would be more
            difficult to determine.
       */
        val questionMarksAroundTheWorld = "[?;;؟՞፧？]"

        val text = note.comments?.firstOrNull()?.text
        return text?.matches(".*$questionMarksAroundTheWorld.*".toRegex()) ?: false
    }
}
