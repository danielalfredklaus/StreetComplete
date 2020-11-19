package ch.uzh.ifi.accesscomplete.ktx

import android.content.ContentValues

operator fun ContentValues.plus(b: ContentValues) = ContentValues(this).also { it.putAll(b) }
