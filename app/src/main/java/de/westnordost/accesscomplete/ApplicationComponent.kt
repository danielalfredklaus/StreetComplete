package de.westnordost.accesscomplete

import dagger.Component
import de.westnordost.accesscomplete.about.PrivacyStatementFragment
import de.westnordost.accesscomplete.controls.*
import de.westnordost.accesscomplete.data.DbModule
import de.westnordost.accesscomplete.data.OsmApiModule
import de.westnordost.accesscomplete.data.download.DownloadModule
import de.westnordost.accesscomplete.data.download.QuestDownloadService
import de.westnordost.accesscomplete.data.meta.MetadataModule
import de.westnordost.accesscomplete.data.osm.upload.changesets.ChangesetAutoCloserWorker
import de.westnordost.accesscomplete.data.osmnotes.OsmNotesModule
import de.westnordost.accesscomplete.data.upload.UploadModule
import de.westnordost.accesscomplete.data.upload.UploadModule2
import de.westnordost.accesscomplete.data.upload.UploadService
import de.westnordost.accesscomplete.data.user.UserModule
import de.westnordost.accesscomplete.data.user.achievements.AchievementsModule
import de.westnordost.accesscomplete.map.MainFragment
import de.westnordost.accesscomplete.map.MapFragment
import de.westnordost.accesscomplete.map.MapModule
import de.westnordost.accesscomplete.map.QuestsMapFragment
import de.westnordost.accesscomplete.measurement.ARCoreMeasurementActivity
import de.westnordost.accesscomplete.notifications.OsmUnreadMessagesFragment
import de.westnordost.accesscomplete.quests.AAddLocalizedNameForm
import de.westnordost.accesscomplete.quests.AbstractQuestAnswerFragment
import de.westnordost.accesscomplete.quests.QuestModule
import de.westnordost.accesscomplete.quests.SplitWayFragment
import de.westnordost.accesscomplete.quests.note_discussion.NoteDiscussionForm
import de.westnordost.accesscomplete.settings.OAuthFragment
import de.westnordost.accesscomplete.settings.SettingsActivity
import de.westnordost.accesscomplete.settings.SettingsFragment
import de.westnordost.accesscomplete.settings.ShowQuestFormsActivity
import de.westnordost.accesscomplete.settings.questselection.QuestSelectionFragment
import de.westnordost.accesscomplete.user.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    UploadModule2::class,
    OsmApiModule::class,
    OsmNotesModule::class,
    UploadModule::class,
    DownloadModule::class,
    QuestModule::class,
    DbModule::class,
    MetadataModule::class,
    UserModule::class,
    AchievementsModule::class,
    MapModule::class
])
interface ApplicationComponent {
    fun inject(app: AccessCompleteApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(mapFragment: MapFragment)
    fun inject(noteDiscussionForm: NoteDiscussionForm)
    fun inject(uploadService: UploadService)
    fun inject(questChangesDownloadService: QuestDownloadService)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(settingsActivity: SettingsActivity)
    fun inject(OAuthFragment: OAuthFragment)
    fun inject(questStatisticsFragment: QuestStatisticsFragment)
    fun inject(fields: AAddLocalizedNameForm.InjectedFields)
    fun inject(fields: AbstractQuestAnswerFragment.InjectedFields)
    fun inject(questsMapFragment: QuestsMapFragment)
    fun inject(questSelectionFragment: QuestSelectionFragment)
    fun inject(worker: ChangesetAutoCloserWorker)
    fun inject(splitWayFragment: SplitWayFragment)
    fun inject(showQuestFormsActivity: ShowQuestFormsActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(achievementsFragment: AchievementsFragment)
    fun inject(linksFragment: LinksFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(userActivity: UserActivity)
    fun inject(loginFragment: LoginFragment)
    fun inject(osmUnreadMessagesFragment: OsmUnreadMessagesFragment)
    fun inject(notificationButtonFragment: NotificationButtonFragment)
    fun inject(undoButtonFragment: UndoButtonFragment)
    fun inject(uploadButtonFragment: UploadButtonFragment)
    fun inject(answersCounterFragment: AnswersCounterFragment)
    fun inject(downloadProgressFragment: DownloadProgressFragment)
    fun inject(questStatisticsByCountryFragment: QuestStatisticsByCountryFragment)
    fun inject(questStatisticsByQuestTypeFragment: QuestStatisticsByQuestTypeFragment)
    fun inject(privacyStatementFragment: PrivacyStatementFragment)
    fun inject(measurementActivity: ARCoreMeasurementActivity)
}
