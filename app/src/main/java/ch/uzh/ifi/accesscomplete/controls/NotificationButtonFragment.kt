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

package ch.uzh.ifi.accesscomplete.controls

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.notifications.Notification
import ch.uzh.ifi.accesscomplete.data.notifications.NotificationsSource
import ch.uzh.ifi.accesscomplete.ktx.popIn
import ch.uzh.ifi.accesscomplete.ktx.popOut
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
