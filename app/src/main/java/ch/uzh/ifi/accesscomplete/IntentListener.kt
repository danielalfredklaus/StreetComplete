package ch.uzh.ifi.accesscomplete

import android.content.Intent

interface IntentListener {
    fun onNewIntent(intent: Intent)
}
