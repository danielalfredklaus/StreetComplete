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

import ch.uzh.ifi.osmapi.map.MapData
import ch.uzh.ifi.osmapi.map.getRelationComplete
import ch.uzh.ifi.osmapi.map.getWayComplete
import de.westnordost.osmapi.common.errors.OsmNotFoundException
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.Node
import de.westnordost.osmapi.map.data.Relation
import de.westnordost.osmapi.map.data.Way
import ch.uzh.ifi.accesscomplete.data.MapDataApi
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometryCreator
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.MergedElementDao
import javax.inject.Inject

/** When an element has been updated or deleted (from the API), this class takes care of updating
 *  the element and the data that is dependent on the element - the quests */
class OsmElementUpdateController @Inject constructor(
    private val mapDataApi: MapDataApi,
    private val elementGeometryCreator: ElementGeometryCreator,
    private val elementDB: MergedElementDao,
    private val questGiver: OsmQuestGiver,
){

    /** The [element] has been updated. Persist that, determine its geometry and update the quests
     *  based on that element. If [recreateQuestTypes] is not null, always (re)create the given
     *  quest types on the element without checking for its eligibility */
    fun update(element: Element, recreateQuestTypes: List<OsmElementQuestType<*>>?) {
        val newGeometry = createGeometry(element)
        if (newGeometry != null) {
            elementDB.put(element)

            if (recreateQuestTypes == null) {
                questGiver.updateQuests(element, newGeometry)
            } else {
                questGiver.recreateQuests(element, newGeometry, recreateQuestTypes)
            }
        } else {
            // new element has invalid geometry
            delete(element.type, element.id)
        }
    }

    fun delete(elementType: Element.Type, elementId: Long) {
        elementDB.delete(elementType, elementId)
        // geometry is deleted by the  osmQuestController
        questGiver.deleteQuests(elementType, elementId)
    }

    fun get(elementType: Element.Type, elementId: Long): Element? {
        return elementDB.get(elementType, elementId)
    }

    private fun createGeometry(element: Element): ElementGeometry? {
        when(element) {
            is Node -> {
                return elementGeometryCreator.create(element)
            }
            is Way -> {
                val mapData: MapData
                try {
                    mapData = mapDataApi.getWayComplete(element.id)
                } catch (e: OsmNotFoundException) {
                    return null
                }
                return elementGeometryCreator.create(element, mapData)
            }
            is Relation -> {
                val mapData: MapData
                try {
                    mapData = mapDataApi.getRelationComplete(element.id)
                } catch (e: OsmNotFoundException) {
                    return null
                }
                return elementGeometryCreator.create(element, mapData)
            }
            else -> return null
        }
    }
}
