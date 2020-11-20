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

package ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo

import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuest
import ch.uzh.ifi.accesscomplete.data.osm.upload.HasElementTagChanges
import ch.uzh.ifi.accesscomplete.data.osm.upload.UploadableInChangeset

/** Contains the information necessary to revert the changes made by a previously uploaded OsmQuest */
class UndoOsmQuest(
        val id: Long?,
        val type: OsmElementQuestType<*>,
        override val elementType: Element.Type,
        override val elementId: Long,
        override val changes: StringMapChanges,
        val changesSource: String,
        val geometry: ElementGeometry
) : UploadableInChangeset, HasElementTagChanges {

    constructor(quest: OsmQuest) : this(
        null, quest.osmElementQuestType, quest.elementType, quest.elementId,
        quest.changes!!.reversed(), quest.changesSource!!, quest.geometry)

    /* can't ask the quest here if it is applicable to the element or not, because the change
       of the revert is exactly the opposite of what the quest would normally change and the
       element ergo has the changes already applied that a normal quest would add */
    override fun isApplicableTo(element: Element) = true

    override val position: LatLon get() = geometry.center

    override val source get() = changesSource
    override val osmElementQuestType  get() = type
}
