package ch.uzh.ifi.accesscomplete.data.upload

import de.westnordost.osmapi.map.data.LatLon

interface OnUploadedChangeListener {
    fun onUploaded(questType: String, at: LatLon)
    fun onDiscarded(questType: String, at: LatLon)
}
