package ch.uzh.ifi.accesscomplete.quests

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import android.view.View

import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import ch.uzh.ifi.accesscomplete.util.getOrientationAtCenterLineInDegrees
import ch.uzh.ifi.accesscomplete.view.StreetSideSelectPuzzle

class StreetSideRotater(
    private val puzzle: StreetSideSelectPuzzle,
    private val compassView: View,
    geometry: ElementPolylinesGeometry
) {
    private val wayOrientationAtCenter = geometry.getOrientationAtCenterLineInDegrees()
    private val uiThread = Handler(Looper.getMainLooper())

    @AnyThread fun onMapOrientation(rotation: Float, tilt: Float) {
        uiThread.post {
            puzzle.setStreetRotation(wayOrientationAtCenter + rotation.toDegrees())
            compassView.rotation = rotation.toDegrees()
            compassView.rotationX = tilt.toDegrees()
        }
    }

    private fun Float.toDegrees() = (180 * this / Math.PI).toFloat()
}
