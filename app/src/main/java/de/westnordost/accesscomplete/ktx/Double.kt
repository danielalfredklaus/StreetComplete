package de.westnordost.accesscomplete.ktx

fun Double.toShortString() = if (this % 1 == 0.0) toInt().toString() else toString()
