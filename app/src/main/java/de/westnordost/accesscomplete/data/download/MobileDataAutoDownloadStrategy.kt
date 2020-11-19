package de.westnordost.accesscomplete.data.download

import javax.inject.Inject
import de.westnordost.accesscomplete.data.download.tiles.DownloadedTilesDao
import de.westnordost.accesscomplete.data.quest.VisibleQuestsSource

/** Download strategy if user is on mobile data */
class MobileDataAutoDownloadStrategy @Inject constructor(
    visibleQuestsSource: VisibleQuestsSource,
    downloadedTilesDao: DownloadedTilesDao
) : AVariableRadiusStrategy(visibleQuestsSource, downloadedTilesDao) {

    override val maxDownloadAreaInKm2 = 6.0 // that's a radius of about 1.4 km
    override val desiredQuestCountInVicinity = 500
}
