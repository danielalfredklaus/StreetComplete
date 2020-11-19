package ch.uzh.ifi.accesscomplete.ktx

fun Boolean.toYesNo(): String = if (this) "yes" else "no"
