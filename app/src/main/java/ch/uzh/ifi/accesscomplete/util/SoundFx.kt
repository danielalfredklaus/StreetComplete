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

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.provider.Settings
import android.util.SparseIntArray
import androidx.annotation.RawRes
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Simple wrapper to enable just playing a sound effect from raw resources */
@Singleton class SoundFx @Inject constructor(private val context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build())
        .build()

    private val soundIds: SparseIntArray = SparseIntArray()

    // map of sampleId -> continuation of sampleId
    private val loadCompleteContinuations = mutableMapOf<Int, Continuation<Int>>()

    init {
        soundPool.setOnLoadCompleteListener { _, soundId, _ ->
            loadCompleteContinuations[soundId]?.resume(soundId)
        }
    }

    // will not return until the loading of the sound is complete
    private suspend fun prepare(@RawRes resId: Int): Int = suspendCoroutine { cont ->
        val soundId = soundPool.load(context, resId, 1)
        loadCompleteContinuations[soundId] = cont
    }

    suspend fun play(@RawRes resId: Int) {
        if (soundIds[resId] == 0) soundIds.put(resId, prepare(resId))
        val isTouchSoundsEnabled = Settings.System.getInt(context.contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0
        if (isTouchSoundsEnabled) soundPool.play(soundIds[resId], 1f, 1f, 1, 0, 1f)
    }
}
