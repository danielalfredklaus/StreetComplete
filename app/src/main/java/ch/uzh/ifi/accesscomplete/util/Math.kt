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

package ch.uzh.ifi.accesscomplete.util

import kotlin.math.PI
import kotlin.math.tan

fun Double.normalizeDegrees(startAt: Double = 0.0): Double {
    var result = this % 360 // is now -360..360
    result = (result + 360) % 360 // is now 0..360
    if (result > startAt + 360) result -= 360
    return result
}

fun Double.normalizeRadians(startAt: Double = 0.0): Double {
    val pi2 = PI*2
    var result = this % pi2 // is now -2PI..2PI
    result = (result + pi2) % pi2 // is now 0..2PI
    if (result > startAt + pi2) result -= pi2
    return result
}

fun Float.normalizeDegrees(startAt: Float = 0f): Float {
    var result = this % 360 // is now -360..360
    result = (result + 360) % 360 // is now 0..360
    if (result > startAt + 360) result -= 360
    return result
}

fun Double.fromDegreesToPercentage(): Double {
    return tan(this  * Math.PI / 180) * 100
}
