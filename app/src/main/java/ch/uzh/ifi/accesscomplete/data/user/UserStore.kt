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

package ch.uzh.ifi.accesscomplete.data.user

import android.content.SharedPreferences
import androidx.core.content.edit
import de.westnordost.osmapi.user.UserDetails
import ch.uzh.ifi.accesscomplete.Prefs
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/** Stores OSM user data.
 *
 *  Must be the only access to these values (=singleton) to ensure that
 *  other classes listening to updates are properly notified. */
@Singleton class UserStore @Inject constructor(private val prefs: SharedPreferences) {

    interface UpdateListener {
        fun onUserDataUpdated()
    }
    private val listeners: MutableList<UpdateListener> = CopyOnWriteArrayList()

    val userId: Long get() = prefs.getLong(Prefs.OSM_USER_ID, -1)
    val userName: String? get() = prefs.getString(Prefs.OSM_USER_NAME, null)

    var rank: Int
        get() = prefs.getInt(Prefs.USER_GLOBAL_RANK, -1)
        set(value) {
            prefs.edit(true) { putInt(Prefs.USER_GLOBAL_RANK, value) }
        }

    var daysActive: Int
        get() = prefs.getInt(Prefs.USER_DAYS_ACTIVE, 0)
        set(value) {
            prefs.edit(true) { putInt(Prefs.USER_DAYS_ACTIVE, value) }
        }

    var lastStatisticsUpdate: Long
    get() = prefs.getLong(Prefs.USER_LAST_TIMESTAMP_ACTIVE, 0)
    set(value) {
        prefs.edit(true) { putLong(Prefs.USER_LAST_TIMESTAMP_ACTIVE, value) }
    }

    var isSynchronizingStatistics: Boolean
        // default true because if it is not set yet, the first thing that is done is to synchronize it
        get() = prefs.getBoolean(Prefs.IS_SYNCHRONIZING_STATISTICS, true)
        set(value) {
            prefs.edit(true) { putBoolean(Prefs.IS_SYNCHRONIZING_STATISTICS, value) }
        }

    var unreadMessagesCount: Int
    get() = prefs.getInt(Prefs.OSM_UNREAD_MESSAGES, 0)
    set(value) {
        prefs.edit(true) { putInt(Prefs.OSM_UNREAD_MESSAGES, value) }
        onUserDetailsUpdated()
    }

    fun setDetails(userDetails: UserDetails) {
        prefs.edit(true) {
            putLong(Prefs.OSM_USER_ID, userDetails.id)
            putString(Prefs.OSM_USER_NAME, userDetails.displayName)
            putInt(Prefs.OSM_UNREAD_MESSAGES, userDetails.unreadMessagesCount)
        }
        onUserDetailsUpdated()
    }

    fun clear() {
        prefs.edit(true) {
            remove(Prefs.OSM_USER_ID)
            remove(Prefs.OSM_USER_NAME)
            remove(Prefs.OSM_UNREAD_MESSAGES)
            remove(Prefs.USER_DAYS_ACTIVE)
            remove(Prefs.IS_SYNCHRONIZING_STATISTICS)
        }
    }

    fun addListener(listener: UpdateListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: UpdateListener) {
        listeners.remove(listener)
    }

    private fun onUserDetailsUpdated() {
        for (listener in listeners) {
            listener.onUserDataUpdated()
        }
    }
}
