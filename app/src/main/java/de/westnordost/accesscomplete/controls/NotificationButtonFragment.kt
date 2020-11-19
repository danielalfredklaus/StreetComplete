package de.westnordost.accesscomplete.controls

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import de.westnordost.accesscomplete.Injector
import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.notifications.Notification
import de.westnordost.accesscomplete.data.notifications.NotificationsSource
import de.westnordost.accesscomplete.ktx.popIn
import de.westnordost.accesscomplete.ktx.popOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Handles showing a button with a little counter that shows how many unread notifications there are */
class NotificationButtonFragment : Fragment(R.layout.fragment_notification_button),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    @Inject lateinit var notificationsSource: NotificationsSource

    interface Listener {
        fun onClickShowNotification(notification: Notification)
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    private val notificationButton get() = view as NotificationButton

    private var notificationsSourceUpdateListener = object : NotificationsSource.UpdateListener {
        override fun onNumberOfNotificationsUpdated(numberOfNotifications: Int) {
            launch(Dispatchers.Main) {
                notificationButton.notificationsCount = numberOfNotifications
                if (notificationButton.isVisible && numberOfNotifications == 0) {
                    notificationButton.popOut()
                } else if(!notificationButton.isVisible && numberOfNotifications > 0) {
                    notificationButton.popIn()
                }
            }
        }
    }

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationButton.setOnClickListener {
            val notification = notificationsSource.popNextNotification()
            if (notification != null) {
                listener?.onClickShowNotification(notification)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val numberOfNotifications = notificationsSource.getNumberOfNotifications()
        notificationButton.notificationsCount = numberOfNotifications
        notificationButton.isGone = numberOfNotifications <= 0
        notificationsSource.addListener(notificationsSourceUpdateListener)
    }

    override fun onStop() {
        super.onStop()
        notificationsSource.removeListener(notificationsSourceUpdateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}
