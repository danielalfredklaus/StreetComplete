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

package ch.uzh.ifi.accesscomplete.data.upload

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

import java.util.concurrent.atomic.AtomicBoolean

import javax.inject.Inject

import de.westnordost.osmapi.common.errors.OsmAuthorizationException
import de.westnordost.osmapi.map.data.LatLon
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesDao
import ch.uzh.ifi.accesscomplete.data.user.UserController
import ch.uzh.ifi.accesscomplete.util.enclosingTile

/** Collects and uploads all changes the user has done: notes he left, comments he left on existing
 * notes and quests he answered  */
class UploadService : IntentService(TAG) {
    @Inject internal lateinit var uploaders: List<Uploader>
    @Inject internal lateinit var versionIsBannedChecker: VersionIsBannedChecker
    @Inject internal lateinit var userController: UserController
    @Inject internal lateinit var downloadedTilesDB: DownloadedTilesDao

    private val binder = Interface()

    // listeners
    private val uploadedChangeRelay = object : OnUploadedChangeListener {
        override fun onUploaded(questType: String, at: LatLon) {
            progressListener?.onProgress(true)
        }

        override fun onDiscarded(questType: String, at: LatLon) {
            invalidateArea(at)
            progressListener?.onProgress(false)
        }
    }
    private var isUploading: Boolean = false
    private var progressListener: UploadProgressListener? = null

    private val cancelState = AtomicBoolean(false)

    private val bannedInfo: BannedInfo by lazy { versionIsBannedChecker.get() }

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        cancelState.set(false)
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        cancelState.set(true)
        super.onDestroy()
    }

    override fun onHandleIntent(intent: Intent?) {
        if (cancelState.get()) return

        isUploading = true
        progressListener?.onStarted()

        try {
            val banned = bannedInfo
            if (banned is IsBanned) {
                throw VersionBannedException(banned.reason)
            }

            // let's fail early in case of no authorization
            if (!userController.isLoggedIn) {
                throw OsmAuthorizationException(401, "Unauthorized", "User is not authorized")
            }

            Log.i(TAG, "Starting upload")

            for (uploader in uploaders) {
                if (cancelState.get()) return
                uploader.uploadedChangeListener = uploadedChangeRelay
                uploader.upload(cancelState)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Unable to upload", e)
            progressListener?.onError(e)
        }

        isUploading = false
        progressListener?.onFinished()

        Log.i(TAG, "Finished upload")
    }

    private fun invalidateArea(pos: LatLon) {
        // called after a conflict. If there is a conflict, the user is not the only one in that
        // area, so best invalidate all downloaded quests here and redownload on next occasion
        val tile = pos.enclosingTile(ApplicationConstants.QUEST_TILE_ZOOM)
        downloadedTilesDB.remove(tile)
    }

    /** Public interface to classes that are bound to this service  */
    inner class Interface : Binder() {
        fun setProgressListener(listener: UploadProgressListener?) {
            progressListener = listener
        }

        val isUploadInProgress: Boolean get() = isUploading
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, UploadService::class.java)
        }

        private const val TAG = "Upload"
    }
}
