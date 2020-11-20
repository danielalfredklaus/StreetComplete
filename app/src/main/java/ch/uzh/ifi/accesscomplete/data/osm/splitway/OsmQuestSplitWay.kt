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

package ch.uzh.ifi.accesscomplete.data.osm.splitway

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osm.upload.UploadableInChangeset

/** Contains all necessary information about where to perform a split of a certain OSM way.
 *
 *  It is assigned to a quest and source because it should be put in the same changeset as the
 *  quest normally would, so that the context in which a way was split is clear for people doing
 *  QA.
 *
 *  Keeping the split positions as a lat-lon position because it more robust when handling
 *  conflicts than if the split positions were kept as node ids or node indices of the way.
 *  */
data class OsmQuestSplitWay(
        val questId: Long,
        val questType: OsmElementQuestType<*>,
        val wayId: Long,
        override val source: String,
        val splits: List<SplitPolylineAtPosition>,
        val questTypesOnWay: List<OsmElementQuestType<*>>) : UploadableInChangeset {

    override val osmElementQuestType get() = questType
    override val elementType get() = Element.Type.WAY
    override val elementId get() = wayId
    override val position: LatLon get() = splits.first().pos
}
