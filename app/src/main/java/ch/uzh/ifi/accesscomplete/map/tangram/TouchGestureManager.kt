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

package ch.uzh.ifi.accesscomplete.map.tangram

import com.mapzen.tangram.MapController
import com.mapzen.tangram.TouchInput

/**
 *  Manages touch gesture responders. Use in place of directly setting the responders on the
 *  touchInput.
 *
 *  TouchInput Responders set via the Tangram MapController.touchInput (0.12.0) completely override
 *  the default behavior, independent of what they return in on*Begin().
 *
 *  A responder set via this class will default to the built-in gesture handling behavior
 *  if the custom responder does return false (= not consume the event).

 *  See https://github.com/tangrams/tangram-es/issues/1960
 *  */
class TouchGestureManager(private val c: MapController) {

    // the getters actually do not get but _create_ the responders, so we need to keep them as fields
    private val defaultShoveResponder = c.shoveResponder
    private val defaultRotateResponse = c.rotateResponder
    private val defaultPanResponder = c.panResponder
    private val defaultScaleResponder = c.scaleResponder

    fun setShoveResponder(responder: TouchInput.ShoveResponder?) {
        if (responder == null) {
            c.touchInput.setShoveResponder(defaultShoveResponder)
        }
        else {
            c.touchInput.setShoveResponder(object : TouchInput.ShoveResponder {
                override fun onShoveBegin(): Boolean {
                    if (responder.onShoveBegin()) return false
                    return defaultShoveResponder.onShoveBegin()
                }

                override fun onShove(distance: Float): Boolean {
                    if (responder.onShove(distance)) return false
                    return defaultShoveResponder.onShove(distance)
                }
                override fun onShoveEnd(): Boolean {
                    responder.onShoveEnd()
                    return defaultShoveResponder.onShoveEnd()
                }
            })
        }
    }

    fun setScaleResponder(responder: TouchInput.ScaleResponder?) {
        if (responder == null) {
            c.touchInput.setScaleResponder(defaultScaleResponder)
        }
        else {
            c.touchInput.setScaleResponder(object : TouchInput.ScaleResponder {
                override fun onScaleBegin(): Boolean {
                    if (responder.onScaleBegin()) return false
                    return defaultScaleResponder.onScaleBegin()
                }

                override fun onScale(x: Float, y: Float, scale: Float, velocity: Float): Boolean {
                    if (responder.onScale(x, y, scale, velocity)) return false
                    return defaultScaleResponder.onScale(x, y, scale, velocity)
                }

                override fun onScaleEnd(): Boolean {
                    responder.onScaleEnd()
                    return defaultScaleResponder.onScaleEnd()
                }
            })
        }
    }

    fun setRotateResponder(responder: TouchInput.RotateResponder?) {
        if (responder == null) {
            c.touchInput.setRotateResponder(defaultRotateResponse)
        }
        else {
            c.touchInput.setRotateResponder(object : TouchInput.RotateResponder {
                override fun onRotateBegin(): Boolean {
                    if (responder.onRotateBegin()) return false
                    return defaultRotateResponse.onRotateBegin()
                }

                override fun onRotate(x: Float, y: Float, rotation: Float): Boolean {
                    if (responder.onRotate(x, y, rotation)) return false
                    return defaultRotateResponse.onRotate(x, y, rotation)
                }

                override fun onRotateEnd(): Boolean {
                    responder.onRotateEnd()
                    return defaultRotateResponse.onRotateEnd()
                }
            })
        }
    }

    fun setPanResponder(responder: TouchInput.PanResponder?) {
        if (responder == null) {
            c.touchInput.setPanResponder(defaultPanResponder)
        }
        else {
            c.touchInput.setPanResponder(object : TouchInput.PanResponder {
                override fun onPanBegin(): Boolean {
                    if (responder.onPanBegin()) return false
                    return defaultPanResponder.onPanBegin()
                }

                override fun onPan(startX: Float, startY: Float, endX: Float, endY: Float ): Boolean {
                    if (responder.onPan(startX, startY, endX, endY)) return false
                    return defaultPanResponder.onPan(startX, startY, endX, endY)
                }

                override fun onPanEnd(): Boolean {
                    responder.onPanEnd()
                    return defaultPanResponder.onPanEnd()
                }

                override fun onFling(posX: Float, posY: Float, velocityX: Float, velocityY: Float): Boolean {
                    if (responder.onFling(posX, posY, velocityX, velocityY)) return false
                    return defaultPanResponder.onFling(posX, posY, velocityX, velocityY)
                }

                override fun onCancelFling(): Boolean {
                    responder.onCancelFling()
                    return defaultPanResponder.onCancelFling()
                }
            })
        }
    }
}
