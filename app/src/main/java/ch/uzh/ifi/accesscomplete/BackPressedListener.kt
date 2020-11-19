package ch.uzh.ifi.accesscomplete

interface BackPressedListener {
    /** Return true to consume the event */
    fun onBackPressed(): Boolean
}
