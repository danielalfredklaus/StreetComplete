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

import android.util.Log
import de.westnordost.osmapi.map.data.BoundingBox
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNotesDownloader
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesDao
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesType
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.*
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmApiQuestDownloader
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import ch.uzh.ifi.accesscomplete.data.user.UserStore
import ch.uzh.ifi.accesscomplete.util.TilesRect
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.max

/** Takes care of downloading all note and osm quests */
class QuestDownloader @Inject constructor(
    private val osmNotesDownloaderProvider: Provider<OsmNotesDownloader>,
    private val osmApiQuestDownloaderProvider: Provider<OsmApiQuestDownloader>,
    private val downloadedTilesDao: DownloadedTilesDao,
    private val questTypeRegistry: QuestTypeRegistry,
    private val userStore: UserStore
) {
    var progressListener: DownloadProgressListener? = null

    @Synchronized fun download(tiles: TilesRect, cancelState: AtomicBoolean) {
        if (cancelState.get()) return

        progressListener?.onStarted()
        if (hasQuestsAlready(tiles)) {
            progressListener?.onSuccess()
            progressListener?.onFinished()
            return
        }

        val bbox = tiles.asBoundingBox(ApplicationConstants.QUEST_TILE_ZOOM)

        Log.i(TAG, "(${bbox.asLeftBottomRightTopString}) Starting")

        try {
            downloadQuestTypes(tiles, bbox, cancelState)
            progressListener?.onSuccess()
        } finally {
            progressListener?.onFinished()
            Log.i(TAG, "(${bbox.asLeftBottomRightTopString}) Finished")
        }
    }

    private fun downloadQuestTypes(tiles: TilesRect, bbox: BoundingBox, cancelState: AtomicBoolean) {
        val downloadItem = DownloadItem(R.drawable.ic_search_black_128dp, "Multi download")
        progressListener?.onStarted(downloadItem)

        // always first download notes, note positions are blockers for creating other quests
        downloadNotes(bbox)

        if (cancelState.get()) return

        downloadOsmElementQuestTypes(bbox)

        downloadedTilesDao.put(tiles, DownloadedTilesType.QUESTS)

        progressListener?.onFinished(downloadItem)
    }

    private fun hasQuestsAlready(tiles: TilesRect): Boolean {
        val questExpirationTime = ApplicationConstants.REFRESH_QUESTS_AFTER
        val ignoreOlderThan = max(0, System.currentTimeMillis() - questExpirationTime)
        return downloadedTilesDao.get(tiles, ignoreOlderThan).contains(DownloadedTilesType.QUESTS)
    }

    private fun downloadNotes(bbox: BoundingBox) {
        val notesDownload = osmNotesDownloaderProvider.get()
        val userId: Long = userStore.userId.takeIf { it != -1L } ?: return
        // do not download notes if not logged in because notes shall only be downloaded if logged in

        val maxNotes = 10000
        notesDownload.download(bbox, userId, maxNotes)
    }

    private fun downloadOsmElementQuestTypes(bbox: BoundingBox) {
        val questTypes = questTypeRegistry.all.filterIsInstance<OsmElementQuestType<*>>()
        osmApiQuestDownloaderProvider.get().download(questTypes, bbox)
    }

    companion object {
        private const val TAG = "QuestDownload"
    }
}
