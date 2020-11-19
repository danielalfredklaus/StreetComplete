package ch.uzh.ifi.accesscomplete.data.osm.upload

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChanges

interface HasElementTagChanges {
    val changes: StringMapChanges?
    fun isApplicableTo(element: Element): Boolean?
}
