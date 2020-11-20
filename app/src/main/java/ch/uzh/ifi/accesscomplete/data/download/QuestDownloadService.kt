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

package ch.uzh.ifi.accesscomplete.data.download

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.util.TilesRect
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Provider

/** Downloads all quests in a given area asynchronously. To use, start the service with the
 * appropriate parameters.
 *
 * Generally, starting a new download cancels the old one. This is a feature; Consideration:
 * If the user requests a new area to be downloaded, he'll generally be more interested in his last
 * request than any request he made earlier and he wants that as fast as possible.
 *
 * The service can be bound to snoop into the state of the downloading process:
 * * To receive progress callbacks
 * * To receive callbacks when new quests are created or old ones removed
 * * To query for the state of the service and/or current download task, i.e. if the current
 * download job was started by the user
 */
class QuestDownloadService : SingleIntentService(TAG) {
    @Inject internal lateinit var questDownloaderProvider: Provider<QuestDownloader>

    private lateinit var notificationController: QuestDownloadNotificationController

    // interface
    private val binder: IBinder = Interface()

    // listener
    private var progressListenerRelay = object : DownloadProgressListener {
        override fun onStarted() { progressListener?.onStarted() }
        override fun onError(e: Exception) { progressListener?.onError(e) }
        override fun onSuccess() {
            isDownloading = false
            progressListener?.onSuccess()
        }
        override fun onFinished() {
            isDownloading = false
            progressListener?.onFinished()
        }
        override fun onStarted(item: DownloadItem) {
            currentDownloadItem = item
            progressListener?.onStarted(item)
        }
        override fun onFinished(item: DownloadItem) {
            currentDownloadItem = null
            progressListener?.onFinished(item)
        }
    }
    private var progressListener: DownloadProgressListener? = null

    // state
    private var isPriorityDownload: Boolean = false
    private var isDownloading: Boolean = false
    set(value) {
        field = value
        if (!value || !showNotification) notificationController.hide()
        else notificationController.show()
    }

    private var showNotification = false
    set(value) {
        field = value
        if (!value || !isDownloading) notificationController.hide()
        else notificationController.show()
    }

    private var currentDownloadItem: DownloadItem? = null

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        notificationController = QuestDownloadNotificationController(
            this, ApplicationConstants.NOTIFICATIONS_CHANNEL_DOWNLOAD, 1)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onHandleIntent(intent: Intent?, cancelState: AtomicBoolean) {
        if (cancelState.get()) return
        if (intent == null) return
        if (intent.getBooleanExtra(ARG_CANCEL, false)) {
            cancel()
            Log.i(TAG, "Download cancelled")
            return
        }

        val tiles = intent.getSerializableExtra(ARG_TILES_RECT) as TilesRect

        val dl = questDownloaderProvider.get()
        dl.progressListener = progressListenerRelay
        try {
            isPriorityDownload = intent.hasExtra(ARG_IS_PRIORITY)
            isDownloading = true
            dl.download(tiles, cancelState)
        } catch (e: Exception) {
            Log.e(TAG, "Unable to download quests", e)
            progressListenerRelay.onError(e)
        }
        isPriorityDownload = false
        isDownloading = false
    }

    /** Public interface to classes that are bound to this service  */
    inner class Interface : Binder() {
        fun setProgressListener(listener: DownloadProgressListener?) {
            progressListener = listener
        }

        val isPriorityDownloadInProgress: Boolean get() = isPriorityDownload

        val isDownloadInProgress: Boolean get() = isDownloading

        val currentDownloadItem: DownloadItem? get() = this@QuestDownloadService.currentDownloadItem

        var showDownloadNotification: Boolean
            get() = showNotification
            set(value) { showNotification = value }
    }

    companion object {
        private const val TAG = "QuestDownload"
        const val ARG_TILES_RECT = "tilesRect"
        const val ARG_IS_PRIORITY = "isPriority"
        const val ARG_CANCEL = "cancel"

        fun createIntent(context: Context, tilesRect: TilesRect?,isPriority: Boolean): Intent {
            val intent = Intent(context, QuestDownloadService::class.java)
            intent.putExtra(ARG_TILES_RECT, tilesRect)
            intent.putExtra(ARG_IS_PRIORITY, isPriority)
            return intent
        }

        fun createCancelIntent(context: Context): Intent {
            val intent = Intent(context, QuestDownloadService::class.java)
            intent.putExtra(ARG_CANCEL, true)
            return intent
        }
    }
}
