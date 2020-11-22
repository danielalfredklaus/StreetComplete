/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
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

package ch.uzh.ifi.accesscomplete.measurement

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.toDp
import kotlinx.android.synthetic.main.activity_measurement.*
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt
import com.google.ar.sceneform.rendering.Color as arColor

class ARCoreMeasurementActivity : AppCompatActivity(), Scene.OnUpdateListener {

    @Inject internal lateinit var prefs: SharedPreferences

    private var arFragment: ArFragment? = null

    private var cubeRenderable: ModelRenderable? = null
    private var distanceCardViewRenderable: ViewRenderable? = null

    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()

    private val hints = mutableListOf<ARCoreMeasurementHint>()
    private var currentHintIndex = 0

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_measurement)
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneformFragment) as ArFragment?

        initRenderable()
        initButtons()
        initHints()

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, _: Plane?, _: MotionEvent? ->
            if (cubeRenderable == null || distanceCardViewRenderable == null) {
                return@setOnTapArPlaneListener
            }
            handleTapMeasurement(hitResult)
        }
    }

    private fun initRenderable() {
        MaterialFactory.makeOpaqueWithColor(this, arColor(Color.WHITE))
            .thenAccept { material: Material? ->
                cubeRenderable = ShapeFactory.makeCylinder(
                    0.02f,
                    0f,
                    Vector3.zero(),
                    material
                )
                cubeRenderable!!.isShadowCaster = false
                cubeRenderable!!.isShadowReceiver = false
            }
            .exceptionally {
                Log.e(TAG, "Exception creating cubeRenderable", it)
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, R.layout.distance_text_layout)
            .build()
            .thenAccept { viewRenderable ->
                distanceCardViewRenderable = viewRenderable
                distanceCardViewRenderable!!.isShadowCaster = false
                distanceCardViewRenderable!!.isShadowReceiver = false
            }
            .exceptionally {
                Log.e(TAG, "Exception creating distanceCardViewRenderable", it)
                return@exceptionally null
            }
    }

    private fun initButtons() {
        clearButton.setOnClickListener { clearAllAnchors() }
        okButton.setOnClickListener {
            prefs.edit().putBoolean(Prefs.HAS_COMPLETED_ARCORE_MEASUREMENT, true).apply()

            val data = Intent()
            data.putExtra(RESULT_ATTRIBUTE_DISTANCE, measureDistance())
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun initHints() {
        var hasCompletedMeasurementAlready = prefs.getBoolean(Prefs.HAS_COMPLETED_ARCORE_MEASUREMENT, false)
        hasCompletedMeasurementAlready = false // TODO sst: remove after testing
        if (!hasCompletedMeasurementAlready) {
            hints.add(ARCoreMeasurementHint(
                R.string.arcore_initial_instructions_title,
                R.string.arcore_initial_instructions,
                null))
        }

        val additionalInstructionsId = intent.getIntExtra(EXTRA_ADDITIONAL_INSTRUCTIONS_ID, 0)
        val additionalInstructionsImageId = intent.getIntExtra(EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID, 0)
        if (additionalInstructionsId != 0) {
            hints.add(ARCoreMeasurementHint(
                R.string.arcore_additional_instructions_title,
                additionalInstructionsId,
                if (additionalInstructionsImageId != 0) additionalInstructionsImageId else null))
        }

        if (hints.isEmpty()) {
            hintLayout.visibility = View.GONE
        } else {
            handleHintActionButtonText()
            setHint(0)
        }

        hintActionButton.setOnClickListener {
            if (currentHintIndex + 1 < hints.size) {
                currentHintIndex++
                setHint(currentHintIndex)
            } else {
                hintLayout.animate()
                    .setDuration(200)
                    .y(-10f)
                    .alpha(0f)
                    .translationY(100f.toDp(applicationContext))
                    .withEndAction { hintLayout.visibility = View.GONE }
                    .start()
            }
            handleHintBackButtonVisibility()
            handleHintActionButtonText()
        }

        hintBackButton.setOnClickListener {
            if (currentHintIndex > 0) {
                currentHintIndex--
                setHint(currentHintIndex)
            }
            handleHintBackButtonVisibility()
            handleHintActionButtonText()
        }
    }

    private fun handleHintBackButtonVisibility() {
        hintBackButton.visibility = if (currentHintIndex > 0) View.VISIBLE else View.GONE
    }

    private fun handleHintActionButtonText() {
        val textId = if (currentHintIndex + 1 >= hints.size) R.string.hide else R.string.next
        hintActionButton.text = resources.getText(textId)
    }

    private fun setHint(index: Int) {
        hintTitleView.text = resources.getText(hints[index].titleId)
        hintTextView.text = resources.getText(hints[index].instructionId)
        if (hints[index].imageId != null) {
            hintImageView.setImageResource(hints[index].imageId!!)
            hintImageView.visibility = View.VISIBLE
        } else {
            hintImageView.visibility = View.GONE
        }
    }

    private fun clearAllAnchors() {
        placedAnchors.clear()
        for (anchorNode in placedAnchorNodes) {
            arFragment!!.arSceneView.scene.removeChild(anchorNode)
            anchorNode.isEnabled = false
            anchorNode.anchor!!.detach()
            anchorNode.setParent(null)
        }
        placedAnchorNodes.clear()

        okButton.visibility = View.INVISIBLE
        clearButton.visibility = View.INVISIBLE
    }

    private fun placeAnchor(hitResult: HitResult, renderable: Renderable?) {
        val anchor = hitResult.createAnchor()
        placedAnchors.add(anchor)

        val anchorNode = AnchorNode(anchor).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }
        placedAnchorNodes.add(anchorNode)

        if (renderable != null) {
            TransformableNode(arFragment!!.transformationSystem)
                .apply {
                    this.rotationController.isEnabled = false
                    this.scaleController.isEnabled = false
                    this.translationController.isEnabled = true
                    this.renderable = renderable
                    setParent(anchorNode)
                    select()
                }
        }

        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.arSceneView.scene.addChild(anchorNode)
    }

    private fun handleTapMeasurement(hitResult: HitResult) {
        when (placedAnchorNodes.size) {
            0 -> {
                placeAnchor(hitResult, cubeRenderable!!)
                clearButton.visibility = View.VISIBLE
            }
            1 -> {
                placeAnchor(hitResult, null)
                drawLine(placedAnchorNodes[0], placedAnchorNodes[1])

                clearButton.visibility = View.VISIBLE
                okButton.visibility = View.VISIBLE
            }
            else -> {
                clearAllAnchors()
                placeAnchor(hitResult, cubeRenderable!!)
            }
        }
    }

    private fun drawLine(node1: AnchorNode, node2: AnchorNode) {
        val point1: Vector3 = node1.worldPosition
        val point2: Vector3 = node2.worldPosition

        val difference = Vector3.subtract(point1, point2)
        val directionFromTopToBottom = difference.normalized()
        val rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up())

        MaterialFactory.makeOpaqueWithColor(
            applicationContext,
            arColor(Color.WHITE)
        )
            .thenAccept { material ->
                val lineNode = Node().apply {
                    val model = ShapeFactory.makeCube(
                        Vector3(0.01f, 0.0f, difference.length()),
                        Vector3.zero(),
                        material
                    ).apply {
                        this.isShadowCaster = false
                        this.isShadowReceiver = false
                    }

                    this.setParent(node1) // Do this before editing world position.
                    this.renderable = model
                    this.worldPosition = Vector3.add(point1, point2).scaled(.5f)
                    this.worldRotation = rotationFromAToB
                }

                TransformableNode(arFragment!!.transformationSystem)
                    .apply {
                        this.setParent(lineNode)
                        this.rotationController.isEnabled = false
                        this.scaleController.isEnabled = false
                        this.translationController.isEnabled = true
                        this.renderable = distanceCardViewRenderable
                        this.localRotation = Quaternion.axisAngle(Vector3.down(), 90.0f)
                        this.localPosition = Vector3(0.0f, 0.01f, 0.0f)
                    }

                // End point is drawn together with the line (and the same parent)
                // so that they do not come out of sync if the plane detection struggles...
                TransformableNode(arFragment!!.transformationSystem)
                    .apply {
                        this.setParent(node1) // Do this before editing world position.
                        this.rotationController.isEnabled = false
                        this.scaleController.isEnabled = false
                        this.translationController.isEnabled = true
                        this.renderable = cubeRenderable
                        this.worldPosition = point2
                    }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onUpdate(frameTime: FrameTime) {
        measureDistance()
    }

    private fun measureDistance(): Float? {
        return if (placedAnchorNodes.size == 2) {
            val distanceMeter = calculateDistance(
                placedAnchorNodes[0].worldPosition,
                placedAnchorNodes[1].worldPosition
            )
            val distanceCentimeter = distanceMeter * 100
            updateDistanceCardText(distanceCentimeter)
            distanceMeter
        } else {
            null
        }
    }

    private fun calculateDistance(objectPose0: Vector3, objectPose1: Vector3): Float {
        return calculateDistance(
            objectPose0.x - objectPose1.x,
            objectPose0.y - objectPose1.y,
            objectPose0.z - objectPose1.z
        )
    }

    private fun calculateDistance(x: Float, y: Float, z: Float): Float {
        return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
    }

    private fun updateDistanceCardText(distanceCentimeter: Float) {
        val distanceFloor = "%.0f".format(distanceCentimeter)
        val distanceText = "$distanceFloor cm"

        val textView = (distanceCardViewRenderable!!.view as LinearLayout)
            .findViewById<TextView>(R.id.distanceCard)
        textView.text = distanceText
    }

    companion object {
        const val REQUEST_CODE_MEASURE_DISTANCE = 0
        const val RESULT_ATTRIBUTE_DISTANCE = "DISTANCE"
        const val EXTRA_ADDITIONAL_INSTRUCTIONS_ID = "ADDITIONAL_INSTRUCTIONS_ID"
        const val EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID = "ADDITIONALINSTRUCTIONS_IMAGE_ID"

        private const val TAG = "ARCoreMeasurement"
        private const val MIN_OPENGL_VERSION = 3.0

        fun checkIsSupportedDevice(activity: Activity, onNotSupported: Runnable) {
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val openGlVersionString = activityManager.deviceConfigurationInfo.glEsVersion
            if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
                Log.e(TAG, "Sceneform requires OpenGL ES $MIN_OPENGL_VERSION or later.")
                onNotSupported.run()
            }

            val availability = ArCoreApk.getInstance().checkAvailability(activity.applicationContext)
            if (availability.isTransient) {
                Handler().postDelayed({ checkIsSupportedDevice(activity, onNotSupported) }, 200)
            } else if (availability.isUnsupported) {
                onNotSupported.run()
            }
        }
    }
}
