package ch.uzh.ifi.accesscomplete

import dagger.Component
import ch.uzh.ifi.accesscomplete.about.PrivacyStatementFragment
import ch.uzh.ifi.accesscomplete.controls.*
import ch.uzh.ifi.accesscomplete.data.DbModule
import ch.uzh.ifi.accesscomplete.data.OsmApiModule
import ch.uzh.ifi.accesscomplete.data.download.DownloadModule
import ch.uzh.ifi.accesscomplete.data.download.QuestDownloadService
import ch.uzh.ifi.accesscomplete.data.meta.MetadataModule
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.ChangesetAutoCloserWorker
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNotesModule
import ch.uzh.ifi.accesscomplete.data.upload.UploadModule
import ch.uzh.ifi.accesscomplete.data.upload.UploadModule2
import ch.uzh.ifi.accesscomplete.data.upload.UploadService
import ch.uzh.ifi.accesscomplete.data.user.UserModule
import ch.uzh.ifi.accesscomplete.data.user.achievements.AchievementsModule
import ch.uzh.ifi.accesscomplete.map.MainFragment
import ch.uzh.ifi.accesscomplete.map.MapFragment
import ch.uzh.ifi.accesscomplete.map.MapModule
import ch.uzh.ifi.accesscomplete.map.QuestsMapFragment
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity
import ch.uzh.ifi.accesscomplete.notifications.OsmUnreadMessagesFragment
import ch.uzh.ifi.accesscomplete.quests.AAddLocalizedNameForm
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.quests.QuestModule
import ch.uzh.ifi.accesscomplete.quests.SplitWayFragment
import ch.uzh.ifi.accesscomplete.quests.note_discussion.NoteDiscussionForm
import ch.uzh.ifi.accesscomplete.settings.OAuthFragment
import ch.uzh.ifi.accesscomplete.settings.SettingsActivity
import ch.uzh.ifi.accesscomplete.settings.SettingsFragment
import ch.uzh.ifi.accesscomplete.settings.ShowQuestFormsActivity
import ch.uzh.ifi.accesscomplete.settings.questselection.QuestSelectionFragment
import ch.uzh.ifi.accesscomplete.user.*
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
