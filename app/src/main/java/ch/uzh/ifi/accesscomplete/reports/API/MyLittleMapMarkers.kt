package ch.uzh.ifi.accesscomplete.reports.API

import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.map.MapFragment
import ch.uzh.ifi.accesscomplete.map.tangram.KtMapController
import ch.uzh.ifi.accesscomplete.map.tangram.Marker
import ch.uzh.ifi.accesscomplete.map.tangram.toLngLat
import com.mapzen.tangram.MapData
import com.mapzen.tangram.geometry.Point
import de.westnordost.osmapi.map.data.OsmLatLon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

//TODO: Just add something to the map already
class MyLittleMapMarkers(mF: MapFragment, mC: KtMapController): LifecycleObserver, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val mapFragment: MapFragment = mF
    val mapController: KtMapController = mC
    var questsDisplayed: MutableList<Marker> = mutableListOf()
    var dansLayer: MapData? = null

    init {
        dansLayer = mapController.addDataLayer("DansLayer")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        Log.d(TAG, "onStart Called, adding Marker")
        val marker = mapController.addMarker()
        Log.d(TAG, "Added marker ${marker.markerId}")
        val markerPos = OsmLatLon(47.376098, 8.548037)
        val iconName = mapFragment.resources.getResourceEntryName(R.drawable.ic_placeholder_quest)
        marker.setPoint(markerPos)
        marker.setDrawable(R.drawable.ic_placeholder_quest)
        marker.setDrawOrder(7000)
        marker.setStylingFromString(
            "{ style: 'quest-selection', color: 'white', size: [10px, 10px], flat: false, collide: false, offset: ['0px', '0px'] }"
        )
        val p = Point(markerPos.toLngLat(), mutableMapOf("type" to "point","kind" to iconName, "importance" to counter++.toString()))
        dansLayer?.setFeatures(listOf(p))
        questsDisplayed.add(marker)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        questsDisplayed.forEach{
            mapController.removeMarker(it)
        }
        questsDisplayed = mutableListOf()
        Toast.makeText(mapFragment.requireContext(),"$TAG onStop Called, removed Marker",Toast.LENGTH_SHORT).show()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){

    }

    companion object{
        const val TAG = "MyLittleMapMarkers"
        var counter = 6000
    }
}
