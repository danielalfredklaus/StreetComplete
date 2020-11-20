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

package ch.uzh.ifi.accesscomplete.data.osmnotes

import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNoteDao
import javax.inject.Inject

/** Supplies a set of note positions, these are used to block the creation of other quests.
 *
 *  The note positions include the positions of OSM notes plus those notes that have been created
 *  locally but have not been uploaded yet. */
class NotePositionsSource @Inject constructor(
    private val noteDao: NoteDao,
    private val createNoteDao: CreateNoteDao
) {
    /** Get the positions of all notes within the given bounding box */
    fun getAllPositions(bbox: BoundingBox): List<LatLon> {
        return createNoteDao.getAllPositions(bbox) + noteDao.getAllPositions(bbox)
    }
}
