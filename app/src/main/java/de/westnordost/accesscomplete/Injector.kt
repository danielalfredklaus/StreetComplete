package de.westnordost.accesscomplete

import de.westnordost.accesscomplete.data.DbModule
import de.westnordost.accesscomplete.data.OsmApiModule
import de.westnordost.accesscomplete.data.download.DownloadModule
import de.westnordost.accesscomplete.data.meta.MetadataModule
import de.westnordost.accesscomplete.data.osmnotes.OsmNotesModule
import de.westnordost.accesscomplete.data.upload.UploadModule
import de.westnordost.accesscomplete.data.user.UserModule
import de.westnordost.accesscomplete.data.user.achievements.AchievementsModule
import de.westnordost.accesscomplete.map.MapModule
import de.westnordost.accesscomplete.quests.QuestModule

object Injector {

    lateinit var applicationComponent: ApplicationComponent
        private set

    fun initializeApplicationComponent(app: AccessCompleteApplication?) {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(app!!)) // not sure why it is necessary to add these all by hand, I must be doing something wrong
            .achievementsModule(AchievementsModule)
            .dbModule(DbModule)
            .downloadModule(DownloadModule)
            .metadataModule(MetadataModule)
            .osmApiModule(OsmApiModule)
            .osmNotesModule(OsmNotesModule)
            .questModule(QuestModule)
            .uploadModule(UploadModule)
            .userModule(UserModule)
            .mapModule(MapModule)
            .build()
    }
}
