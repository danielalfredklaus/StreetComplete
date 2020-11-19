package de.westnordost.accesscomplete.data.osm.upload

import de.westnordost.osmapi.map.data.Element
import de.westnordost.accesscomplete.data.osm.changes.StringMapChanges

interface HasElementTagChanges {
    val changes: StringMapChanges?
    fun isApplicableTo(element: Element): Boolean?
}
