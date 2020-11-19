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
