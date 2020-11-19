package ch.uzh.ifi.osmapi.map

import de.westnordost.accesscomplete.data.MapDataApi
import de.westnordost.osmapi.map.data.BoundingBox

fun MapDataApi.getMap(bounds: BoundingBox): MapData {
    val result = MutableMapData()
    getMap(bounds, result)
    return result
}

fun MapDataApi.getWayComplete(id: Long): MapData {
    val result = MutableMapData()
    getWayComplete(id, result)
    return result
}

fun MapDataApi.getRelationComplete(id: Long): MapData {
    val result = MutableMapData()
    getRelationComplete(id, result)
    return result
}
