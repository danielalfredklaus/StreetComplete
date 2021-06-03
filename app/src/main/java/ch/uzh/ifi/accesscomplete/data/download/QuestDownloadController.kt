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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import de.westnordost.osmapi.map.data.BoundingBox
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.util.enclosingTilesRect
import javax.inject.Inject
import javax.inject.Singleton

/** Controls quest downloading */
@Singleton class QuestDownloadController @Inject constructor(
    private val context: Context
): DownloadProgressSource {

    private var downloadServiceIsBound: Boolean = false
    private var downloadService: QuestDownloadService.Interface? = null
    private val downloadServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            downloadService = service as QuestDownloadService.Interface
            downloadService?.setProgressListener(downloadProgressRelay)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            downloadService = null
        }
    }
    private val downloadProgressRelay = DownloadProgressRelay()

    /** @return true if a quest download triggered by the user is running */
    override val isPriorityDownloadInProgress: Boolean get() =
        downloadService?.isPriorityDownloadInProgress == true

    /** @return true if a quest download is running */
    override val isDownloadInProgress: Boolean get() =
        downloadService?.isDownloadInProgress == true

    /** @return the item that is currently being downloaded or null if nothing is downloaded */
    override val currentDownloadItem: DownloadItem? get() =
        downloadService?.currentDownloadItem

    var showNotification: Boolean
        get() = downloadService?.showDownloadNotification == true
        set(value) { downloadService?.showDownloadNotification = value }

    init {
        bindServices()
    }

    /** Download quests in at least the given bounding box asynchronously. The next-bigger rectangle
     * in a (z16) tiles grid that encloses the given bounding box will be downloaded.
     *
     * @param bbox the minimum area to download
     * @param isPriority whether this shall be a priority download (cancels previous downloads and
     * puts itself in the front)
     */
    fun download(bbox: BoundingBox, isPriority: Boolean = false) {
        val tilesRect = bbox.enclosingTilesRect(ApplicationConstants.QUEST_TILE_ZOOM)
        context.startService(QuestDownloadService.createIntent(context, tilesRect, isPriority))
        //TODO: Is this where I put in the additional download source in? The api stuff? Since this is probably the function that gets triggered to download quests
    }

    private fun bindServices() {
        downloadServiceIsBound = context.bindService(
            Intent(context, QuestDownloadService::class.java),
            downloadServiceConnection, Context.BIND_AUTO_CREATE
        )
    }

    private fun unbindServices() {
        if (downloadServiceIsBound) context.unbindService(downloadServiceConnection)
        downloadServiceIsBound = false
    }

    override fun addDownloadProgressListener(listener: DownloadProgressListener) {
        downloadProgressRelay.addListener(listener)
    }
    override fun removeDownloadProgressListener(listener: DownloadProgressListener) {
        downloadProgressRelay.removeListener(listener)
    }
}
