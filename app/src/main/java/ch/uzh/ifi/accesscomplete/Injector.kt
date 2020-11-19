package ch.uzh.ifi.accesscomplete

import ch.uzh.ifi.accesscomplete.data.DbModule
import ch.uzh.ifi.accesscomplete.data.OsmApiModule
import ch.uzh.ifi.accesscomplete.data.download.DownloadModule
import ch.uzh.ifi.accesscomplete.data.meta.MetadataModule
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNotesModule
import ch.uzh.ifi.accesscomplete.data.upload.UploadModule
import ch.uzh.ifi.accesscomplete.data.user.UserModule
import ch.uzh.ifi.accesscomplete.data.user.achievements.AchievementsModule
import ch.uzh.ifi.accesscomplete.map.MapModule
import ch.uzh.ifi.accesscomplete.quests.QuestModule

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
