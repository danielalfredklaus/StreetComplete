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

package ch.uzh.ifi.accesscomplete.ktx

import android.graphics.Point
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.os.postDelayed
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun View.popIn(): ViewPropertyAnimator {
    visibility = View.VISIBLE
    return animate()
        .alpha(1f).scaleX(1f).scaleY(1f)
        .setDuration(100)
        .setInterpolator(DecelerateInterpolator())
        .withEndAction(null)
}

fun View.popOut(): ViewPropertyAnimator {
    return animate()
        .alpha(0f).scaleX(0.5f).scaleY(0.5f)
        .setDuration(100)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction { visibility = View.GONE }
}

suspend fun View.awaitLayout() = suspendCoroutine<Unit> { cont -> doOnLayout { cont.resume(Unit) }}
suspend fun View.awaitPreDraw() = suspendCoroutine<Unit> { cont -> doOnPreDraw { cont.resume(Unit) }}

fun View.getLocationInWindow(): Point {
    val mapPosition = IntArray(2)
    getLocationInWindow(mapPosition)
    return Point(mapPosition[0], mapPosition[1])
}

fun View.showTapHint(initialDelay: Long = 300, pressedDelay: Long = 600) {
    handler?.postDelayed(initialDelay) {
        isPressed = true
        handler?.postDelayed(pressedDelay) {
            isPressed = false
        }
    }
}
