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

import ch.uzh.ifi.accesscomplete.data.MapDataApi
import javax.inject.Inject
import de.westnordost.osmapi.common.errors.OsmConflictException
import de.westnordost.osmapi.map.data.*
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges
import ch.uzh.ifi.accesscomplete.data.osm.upload.*
import ch.uzh.ifi.accesscomplete.ktx.copy
import ch.uzh.ifi.accesscomplete.util.distanceTo

import java.net.HttpURLConnection.HTTP_CONFLICT

/** Uploads the changes made for one quest
 *  Returns the element that has been updated or throws a ConflictException */
class SingleOsmElementTagChangesUploader @Inject constructor(private val mapDataApi: MapDataApi) {

    fun upload(changesetId: Long, quest: HasElementTagChanges, dbElement: Element): Element {
        var element = dbElement
        var handlingConflict = false

        while(true) {
            val changes = quest.changes ?: throw ElementConflictException("No changes")

            val elementWithChangesApplied = element.changesApplied(changes)
            val handler = UpdateElementsHandler()
            try {
                /* only necessary because of #1408: Elements where the version is invalid need to be
                   treated as element conflicts so that the element can be updated from server,
                   otherwise all users who already downloaded those incomplete elements are stuck
                   and can no longer upload anything see also
                   https://github.com/openstreetmap/operations/issues/303 */
                if (element.version < 0)
                    throw OsmConflictException(HTTP_CONFLICT, "Conflict", "Invalid element version")

                mapDataApi.uploadChanges(changesetId, setOf(elementWithChangesApplied), handler) //TODO: Upload to own API, should probably mimick this, or not

            } catch (e: OsmConflictException) {
                if (handlingConflict) {
                    throw ElementConflictException("API reports conflict even after update", e)
                }
                handlingConflict = true
                element = handleConflict(quest, element, e)
                // try again (go back to beginning of loop)
                continue
            }
            return handler.getElementUpdates(listOf(elementWithChangesApplied)).updated.single()
        }
    }

    private fun handleConflict(quest: HasElementTagChanges, element: Element, e: OsmConflictException): Element {
        /* Conflict can either happen because of the changeset or because of the element(s)
           uploaded. A changeset conflict cannot be handled here */
        val newElement = element.fetchUpdated()
        if (newElement?.version == element.version) throw ChangesetConflictException(e.message, e)

        if (newElement == null) {
            throw ElementDeletedException("Element has already been deleted")
        }

        if (isGeometrySubstantiallyDifferent(element, newElement)) {
            throw ElementIncompatibleException("Element geometry changed substantially")
        }

        /* if after updating to the new version of the element, the quest is not applicable to
           the element anymore, drop it (#720) */
        if (quest.isApplicableTo(newElement) == false) {
            throw ElementConflictException("Quest no longer applicable to the element")
        }
        return newElement
    }

    private fun Element.fetchUpdated() =
        when (this) {
            is Node -> mapDataApi.getNode(id)
            is Way -> mapDataApi.getWay(id)
            is Relation -> mapDataApi.getRelation(id)
            else -> null
        }
}

private fun isGeometrySubstantiallyDifferent(element: Element, newElement: Element) =
    when (element) {
        is Node -> isNodeGeometrySubstantiallyDifferent(element, newElement as Node)
        is Way -> isWayGeometrySubstantiallyDifferent(element, newElement as Way)
        is Relation -> isRelationGeometrySubstantiallyDifferent(element, newElement as Relation)
        else -> false
    }

private fun isNodeGeometrySubstantiallyDifferent(node: Node, newNode: Node) =
    /* Moving the node a distance beyond what would pass as adjusting the position within a
       building counts as substantial change. Also, the maximum distance should be not (much)
       bigger than the usual GPS inaccuracy in the city. */
    node.position.distanceTo(newNode.position) > 20

private fun isWayGeometrySubstantiallyDifferent(way: Way, newWay: Way) =
    /* if the first or last node is different, it means that the way has either been extended or
       shortened at one end, which is counted as being substantial:
       If for example the surveyor has been asked to determine something for a certain way
       and this way is now longer, his answer does not apply to the whole way anymore, so that
       is an unsolvable conflict. */
    way.nodeIds.firstOrNull() != newWay.nodeIds.firstOrNull() ||
            way.nodeIds.lastOrNull() != newWay.nodeIds.lastOrNull()

private fun isRelationGeometrySubstantiallyDifferent(relation: Relation, newRelation: Relation) =
    /* a relation is counted as substantially different, if any member changed, even if just
       the order changed because for some relations, the order has an important meaning */
    relation.members != newRelation.members


private fun Element.changesApplied(changes: StringMapChanges): Element {
    val copy = this.copy()
    try {
        if (copy.tags == null) throw ElementConflictException("The element has no tags")
        changes.applyTo(copy.tags)
    } catch (e: IllegalStateException) {
        throw ElementConflictException("Conflict while applying the changes")
    } catch (e: IllegalArgumentException) {
        /* There is a max key/value length limit of 255 characters in OSM. If we reach this
           point, it means the UI did permit an input of more than that. So, we have to catch
           this here latest.
           This is a warning because the UI should prevent this in the first place, at least
           for free-text input. For structured input, like opening hours, it is another matter
           because it's awkward to explain to a non-technical user this technical limitation

           See also https://github.com/openstreetmap/openstreetmap-website/issues/2025
          */
        throw ElementConflictException("Key or value is too long")
    }
    return copy
}
