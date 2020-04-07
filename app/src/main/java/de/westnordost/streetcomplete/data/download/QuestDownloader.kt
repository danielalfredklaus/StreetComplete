package de.westnordost.streetcomplete.data.download

import android.util.Log
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.streetcomplete.ApplicationConstants
import de.westnordost.streetcomplete.data.quest.QuestType
import de.westnordost.streetcomplete.data.quest.QuestTypeRegistry
import de.westnordost.streetcomplete.data.osm.osmquest.OsmElementQuestType
import de.westnordost.streetcomplete.data.osm.osmquest.OsmQuestDownloader
import de.westnordost.streetcomplete.data.osmnotes.notequests.OsmNoteQuestType
import de.westnordost.streetcomplete.data.osmnotes.OsmNotesDownloader
import de.westnordost.streetcomplete.data.download.tiles.DownloadedTilesDao
import de.westnordost.streetcomplete.data.osmnotes.NotePositionsSource
import de.westnordost.streetcomplete.data.user.UserStore
import de.westnordost.streetcomplete.data.visiblequests.OrderedVisibleQuestTypesProvider
import de.westnordost.streetcomplete.util.TilesRect
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.max
import kotlin.math.min

class QuestDownloader @Inject constructor(
    private val osmNotesDownloaderProvider: Provider<OsmNotesDownloader>,
    private val osmQuestDownloaderProvider: Provider<OsmQuestDownloader>,
    private val downloadedTilesDao: DownloadedTilesDao,
    private val notePositionsSource: NotePositionsSource,
    private val questTypeRegistry: QuestTypeRegistry,
    private val questTypesProvider: OrderedVisibleQuestTypesProvider,
    private val userStore: UserStore
) {
    var progressListener: QuestDownloadProgressListener? = null

    // state
    private var downloadedQuestTypes = 0
    private var totalQuestTypes = 0

    @Synchronized fun download(tiles: TilesRect, maxQuestTypes: Int?, cancelState: AtomicBoolean) {
        if (cancelState.get()) return

        val questTypes = getQuestTypesToDownload(tiles, maxQuestTypes)
        if (questTypes.isEmpty()) {
            progressListener?.onNotStarted()
            return
        }

        totalQuestTypes = questTypes.size
        downloadedQuestTypes = 0

        val bbox = tiles.asBoundingBox(ApplicationConstants.QUEST_TILE_ZOOM)

        Log.i(TAG, "(${bbox.asLeftBottomRightTopString}) Starting")
        Log.i(TAG, "Quest types to download: ${questTypes.joinToString { it.javaClass.simpleName }}")

        progressListener?.onStarted()
        try {
            // always first download notes, because note positions are blockers for creating other
            // quests
            if (questTypes.contains(getOsmNoteQuestType())) {
                downloadNotes(bbox, tiles)
            }

            val notesPositions = notePositionsSource.getAllPositions(bbox).toSet()

            for (questType in questTypes) {
                if (cancelState.get()) break
                downloadQuestType(bbox, tiles, questType, notesPositions)
            }
            progressListener?.onSuccess()
        } finally {
            progressListener?.onFinished()
            Log.i(TAG, "(${bbox.asLeftBottomRightTopString}) Finished")
        }
    }

    private fun getOsmNoteQuestType() =
        questTypeRegistry.getByName(OsmNoteQuestType::class.java.simpleName)!!

    private fun getQuestTypesToDownload(tiles: TilesRect, maxQuestTypes: Int?): List<QuestType<*>> {
        val result = questTypesProvider.get().toMutableList()
        val questExpirationTime = ApplicationConstants.REFRESH_QUESTS_AFTER
        val ignoreOlderThan = max(0, System.currentTimeMillis() - questExpirationTime)
        val alreadyDownloadedNames = downloadedTilesDao.get(tiles, ignoreOlderThan)
        if (alreadyDownloadedNames.isNotEmpty()) {
            val alreadyDownloaded = alreadyDownloadedNames.map { questTypeRegistry.getByName(it) }
            result.removeAll(alreadyDownloaded)
            Log.i(TAG, "Quest types already in local store: ${alreadyDownloadedNames.joinToString()}")
        }
        return if (maxQuestTypes != null && maxQuestTypes < result.size)
            result.subList(0, maxQuestTypes)
        else
            result
    }

    private fun downloadNotes(bbox: BoundingBox, tiles: TilesRect) {
        val notesDownload = osmNotesDownloaderProvider.get()
        val userId: Long? = userStore.userId.takeIf { it != -1L }
        // do not download notes if not logged in because notes shall only be downloaded if logged in
        if (userId == null) {
            onProgress()
            return
        }
        val maxNotes = 10000
        notesDownload.download(bbox, userId, maxNotes)
        downloadedTilesDao.put(tiles, OsmNoteQuestType::class.java.simpleName)
        onProgress()
    }

    private fun downloadQuestType(bbox: BoundingBox, tiles: TilesRect, questType: QuestType<*>, notesPositions: Set<LatLon>) {
        if (questType is OsmElementQuestType<*>) {
            val questDownload = osmQuestDownloaderProvider.get()
            if (questDownload.download(questType, bbox, notesPositions)) {
                downloadedTilesDao.put(tiles, questType.javaClass.simpleName)
            }
            onProgress()
        }
    }

    private fun onProgress() {
        downloadedQuestTypes++
        progressListener?.onProgress(min(1f, downloadedQuestTypes.toFloat() / totalQuestTypes))
    }

    companion object {
        private const val TAG = "QuestDownload"
    }

}
