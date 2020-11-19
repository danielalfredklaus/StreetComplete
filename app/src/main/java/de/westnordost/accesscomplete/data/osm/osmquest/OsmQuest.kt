package de.westnordost.accesscomplete.data.osm.osmquest

import java.util.Date

import de.westnordost.accesscomplete.data.quest.Quest
import de.westnordost.accesscomplete.data.quest.QuestStatus
import de.westnordost.accesscomplete.data.osm.changes.StringMapChanges
import de.westnordost.accesscomplete.data.quest.QuestType
import de.westnordost.osmapi.map.data.Element
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.accesscomplete.data.osm.elementgeometry.ElementGeometry
import de.westnordost.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import de.westnordost.accesscomplete.data.osm.upload.HasElementTagChanges
import de.westnordost.accesscomplete.data.osm.upload.UploadableInChangeset
import de.westnordost.accesscomplete.util.measuredLength
import de.westnordost.accesscomplete.util.pointOnPolylineFromEnd
import de.westnordost.accesscomplete.util.pointOnPolylineFromStart

/** Represents one task for the user to complete/correct the data based on one OSM element  */
data class OsmQuest(
    override var id: Long?,
    override val osmElementQuestType: OsmElementQuestType<*>, // underlying OSM data
    override val elementType: Element.Type,
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
