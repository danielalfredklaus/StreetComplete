package ch.uzh.ifi.accesscomplete.quests

import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import ch.uzh.ifi.osmapi.map.MutableMapData
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry

class TestMapDataWithGeometry(elements: Iterable<Element>) : MutableMapData(), MapDataWithGeometry {

    init {
        addAll(elements)
        handle(BoundingBox(0.0,0.0,1.0,1.0))
    }

    val nodeGeometriesById: MutableMap<Long, ElementPointGeometry?> = mutableMapOf()
    val wayGeometriesById: MutableMap<Long, ElementGeometry?> = mutableMapOf()
    val relationGeometriesById: MutableMap<Long, ElementGeometry?> = mutableMapOf()

    override fun getNodeGeometry(id: Long): ElementPointGeometry? = nodeGeometriesById[id]
    override fun getWayGeometry(id: Long): ElementGeometry? = wayGeometriesById[id]
    override fun getRelationGeometry(id: Long): ElementGeometry? = relationGeometriesById[id]
}
