package ch.uzh.ifi.accesscomplete.notifications

import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.about.WhatsNewDialog
import ch.uzh.ifi.accesscomplete.data.notifications.NewAchievementNotification
import ch.uzh.ifi.accesscomplete.data.notifications.NewVersionNotification
import ch.uzh.ifi.accesscomplete.data.notifications.Notification
import ch.uzh.ifi.accesscomplete.data.notifications.OsmUnreadMessagesNotification
import ch.uzh.ifi.accesscomplete.user.AchievementInfoFragment

/** A fragment that contains any fragments that would show notifications.
 *  Usually, notifications are shown as dialogs, however there is currently one exception which
 *  makes this necessary as a fragment */
class NotificationsContainerFragment : Fragment(R.layout.fragment_notifications_container) {

    fun showNotification(notification: Notification) {
        val ctx = context ?: return
        when (notification) {
            is OsmUnreadMessagesNotification -> {
                OsmUnreadMessagesFragment
                    .create(notification.unreadMessages)
                    .show(childFragmentManager, null)
            }
            is NewVersionNotification -> {
                WhatsNewDialog(ctx, notification.sinceVersion)
                    .show()
            }
            is NewAchievementNotification -> {
                val f: Fragment = childFragmentManager.findFragmentById(R.id.achievement_info_fragment)!!
                (f as AchievementInfoFragment).showNew(notification.achievement, notification.level)
            }
        }
    }
}
