package de.westnordost.accesscomplete

interface BackPressedListener {
    /** Return true to consume the event */
    fun onBackPressed(): Boolean
}
