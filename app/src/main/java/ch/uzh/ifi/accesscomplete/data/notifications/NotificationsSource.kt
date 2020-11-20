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

package ch.uzh.ifi.accesscomplete.data.notifications

import android.content.SharedPreferences
import ch.uzh.ifi.accesscomplete.BuildConfig
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.data.user.UserStore
import ch.uzh.ifi.accesscomplete.data.user.achievements.Achievement
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/** This has nothing to do with Android notifications. Android reserves far too many keywords for
 *  itself, really.
 *  This class is to access user notifications, which are basically dialogs that pop up when
 *  clicking on the bell icon, such as "you have a new OSM message in your inbox" etc. */
@Singleton class NotificationsSource @Inject constructor(
    private val userStore: UserStore,
    private val newUserAchievementsDao: NewUserAchievementsDao,
    @Named("Achievements") achievements: List<Achievement>,
    private val prefs: SharedPreferences
) {
    /* Must be a singleton because there is a listener that should respond to a change in the
    *  database table*/

    interface UpdateListener {
        fun onNumberOfNotificationsUpdated(numberOfNotifications: Int)
    }
    private val listeners: MutableList<UpdateListener> = CopyOnWriteArrayList()

    private val achievementsById = achievements.associateBy { it.id }

    init {
        userStore.addListener(object : UserStore.UpdateListener {
            override fun onUserDataUpdated() {
                onNumberOfNotificationsUpdated()
            }
        })
        newUserAchievementsDao.addListener(object : NewUserAchievementsDao.UpdateListener {
            override fun onNewUserAchievementsUpdated() {
                onNumberOfNotificationsUpdated()
            }
        })
    }

    fun addListener(listener: UpdateListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: UpdateListener) {
        listeners.remove(listener)
    }

    fun getNumberOfNotifications(): Int {
        val hasUnreadMessages = userStore.unreadMessagesCount > 0
        val lastVersion = prefs.getString(Prefs.LAST_VERSION, null)
        val hasNewVersion = lastVersion != null && BuildConfig.VERSION_NAME != lastVersion
        if (lastVersion == null) {
            prefs.edit().putString(Prefs.LAST_VERSION, BuildConfig.VERSION_NAME).apply()
        }

        var notifications = 0
        if (hasUnreadMessages) notifications++
        if (hasNewVersion) notifications++
        notifications += newUserAchievementsDao.getCount()
        return notifications
    }

    fun popNextNotification(): Notification? {

        val lastVersion = prefs.getString(Prefs.LAST_VERSION, null)
        if (BuildConfig.VERSION_NAME != lastVersion) {
            prefs.edit().putString(Prefs.LAST_VERSION, BuildConfig.VERSION_NAME).apply()
            if (lastVersion != null) {
                onNumberOfNotificationsUpdated()
                return NewVersionNotification("v$lastVersion")
            }
        }

        val newAchievement = newUserAchievementsDao.pop()
        if (newAchievement != null) {
            onNumberOfNotificationsUpdated()
            val achievement = achievementsById[newAchievement.first]
            if (achievement != null) {
                return NewAchievementNotification(achievement, newAchievement.second)
            }
        }

        val unreadOsmMessages = userStore.unreadMessagesCount
        if (unreadOsmMessages > 0) {
            userStore.unreadMessagesCount = 0
            return OsmUnreadMessagesNotification(unreadOsmMessages)
        }

        return null
    }

    private fun onNumberOfNotificationsUpdated() {
        for (listener in listeners) {
            listener.onNumberOfNotificationsUpdated(getNumberOfNotifications())
        }
    }
}

