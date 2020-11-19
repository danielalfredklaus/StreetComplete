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
