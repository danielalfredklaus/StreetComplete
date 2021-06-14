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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import java.util.Date

import ch.uzh.ifi.accesscomplete.data.quest.Quest
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import ch.uzh.ifi.accesscomplete.data.osm.upload.HasElementTagChanges
import ch.uzh.ifi.accesscomplete.data.osm.upload.UploadableInChangeset
import ch.uzh.ifi.accesscomplete.util.measuredLength
import ch.uzh.ifi.accesscomplete.util.pointOnPolylineFromEnd
import ch.uzh.ifi.accesscomplete.util.pointOnPolylineFromStart

/** Represents one task for the user to complete/correct the data based on one OSM element  */
data class OsmQuest(
    override var id: Long?,
    override val osmElementQuestType: OsmElementQuestType<*>, // underlying OSM data
    override val elementType: Element.Type, //Probably Node, Way, or Relation
    override val elementId: Long,
    override var status: QuestStatus,
    override var changes: StringMapChanges?,
    var changesSource: String?,
    override var lastUpdate: Date,
    override val geometry: ElementGeometry
) : Quest, UploadableInChangeset, HasElementTagChanges {

    constructor(type: OsmElementQuestType<*>, elementType: Element.Type, elementId: Long, geometry: ElementGeometry)
        : this(null, type, elementType, elementId, QuestStatus.NEW, null, null, Date(), geometry)

    override val center: LatLon get() = geometry.center
    override val type: QuestType<*> get() = osmElementQuestType
    override val position: LatLon get() = center

    override val markerLocations: Collection<LatLon> get() {
        if (osmElementQuestType.hasMarkersAtEnds && geometry is ElementPolylinesGeometry) {
            val polyline = geometry.polylines[0]
            val length = polyline.measuredLength()
            if (length > 15 * 4) {
                return listOf(
                    polyline.pointOnPolylineFromStart(15.0)!!,
                    polyline.pointOnPolylineFromEnd(15.0)!!
                )
            }
        }
        return listOf(center)
    }

    override fun isApplicableTo(element: Element) = osmElementQuestType.isApplicableTo(element)

    /* --------------------------- UploadableInChangeset --------------------------- */

    override val source: String get() = changesSource!!
}
