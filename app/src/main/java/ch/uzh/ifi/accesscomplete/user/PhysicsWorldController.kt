/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
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

package ch.uzh.ifi.accesscomplete.user

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World
import java.lang.Runnable
import kotlin.math.max

/** Contains the physics simulation world and the physics simulation loop */
class PhysicsWorldController(gravity: Vec2) : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val world: World = World(gravity)
    private val thread: HandlerThread = HandlerThread("Physics thread")
    private val handler: Handler

    private val loopRunnable = Runnable { loop() }

    private var isRunning = false

    var gravity: Vec2
        get() = world.gravity
        set(value) {
            // wake up everyone if the gravity changed
            world.gravity = value
            var bodyIt = world.bodyList
            while(bodyIt != null) {
                bodyIt.isAwake = true
                bodyIt = bodyIt.next
            }
        }

    interface Listener {
        fun onWorldStep()
    }
    var listener: Listener? = null

    init {
        thread.start()
        handler = Handler(thread.looper)
    }

    fun resume() {
        if (!isRunning) {
            isRunning = true
            handler.postDelayed(loopRunnable, DELAY.toLong())
        }
    }

    fun pause() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(loopRunnable)
        }
    }

    fun destroy() {
        handler.removeCallbacksAndMessages(null)
        thread.quit()
    }

    private fun loop() {
        val startTime = System.currentTimeMillis()
        world.step(DELAY /1000f, 6, 2)
        val executionTime = System.currentTimeMillis() - startTime
        listener?.onWorldStep()
        if (isRunning) {
            handler.postDelayed(this::loop, max(0, DELAY - executionTime))
        }
    }

    suspend fun createBody(def: BodyDef, shape: Shape, density: Float): Body {
        // creating bodies cannot be done while the World is locked (= while world.step(...) is
        // executed), so we must post this on the same thread and then await it to be executed
        return withContext(handler.asCoroutineDispatcher()) {
            val body = world.createBody(def)
            body.createFixture(shape, density)
            body
        }
    }

    companion object {
        private const val DELAY = 16 // 60 fps
    }
}
