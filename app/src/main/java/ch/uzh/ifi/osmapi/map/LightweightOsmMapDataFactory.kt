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

package ch.uzh.ifi.osmapi.map

import de.westnordost.osmapi.changesets.Changeset
import de.westnordost.osmapi.map.MapDataFactory
import de.westnordost.osmapi.map.data.*
import java.util.*

/** Same as OsmMapDataFactory only that it throws away the Changeset data included in the OSM
 *  response */
class LightweightOsmMapDataFactory : MapDataFactory {
    override fun createNode(
        id: Long, version: Int, lat: Double, lon: Double, tags: MutableMap<String, String>?,
        changeset: Changeset?, dateEdited: Date?
    ): Node = OsmNode(id, version, lat, lon, tags, null, dateEdited)

    override fun createWay(
        id: Long, version: Int, nodes: MutableList<Long>, tags: MutableMap<String, String>?,
        changeset: Changeset?, dateEdited: Date?
    ): Way = OsmWay(id, version, nodes, tags, null, dateEdited)

    override fun createRelation(
        id: Long, version: Int, members: MutableList<RelationMember>,
        tags: MutableMap<String, String>?, changeset: Changeset?, dateEdited: Date?
    ): Relation = OsmRelation(id, version, members, tags, null, dateEdited)

    override fun createRelationMember(
        ref: Long, role: String?, type: Element.Type
    ): RelationMember = OsmRelationMember(ref, role, type)
}
