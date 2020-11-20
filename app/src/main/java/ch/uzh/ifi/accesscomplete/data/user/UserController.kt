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

import android.util.Log
import de.westnordost.osmapi.OsmConnection
import ch.uzh.ifi.accesscomplete.data.UserApi
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmAvatarsDownloader
import ch.uzh.ifi.accesscomplete.data.user.achievements.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import oauth.signpost.OAuthConsumer
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class UserController @Inject constructor(
    private val userApi: UserApi,
    private val oAuthStore: OAuthStore,
    private val userStore: UserStore,
    private val userAchievementsDao: UserAchievementsDao,
    private val userLinksDao: UserLinksDao,
    private val avatarsDownloader: OsmAvatarsDownloader,
    private val statisticsUpdater: StatisticsUpdater,
    private val statisticsDao: QuestStatisticsDao,
    private val countryStatisticsDao: CountryStatisticsDao,
    private val osmConnection: OsmConnection
): CoroutineScope by CoroutineScope(Dispatchers.Default), LoginStatusSource, UserAvatarUpdateSource {
    private val loginStatusListeners: MutableList<UserLoginStatusListener> = CopyOnWriteArrayList()
    private val userAvatarListeners: MutableList<UserAvatarListener> = CopyOnWriteArrayList()

    override val isLoggedIn: Boolean get() = oAuthStore.isAuthorized

    fun logIn(consumer: OAuthConsumer) {
        oAuthStore.oAuthConsumer = consumer
        osmConnection.oAuth = consumer
        updateUser()
        loginStatusListeners.forEach { it.onLoggedIn() }
    }

    fun logOut() {
        userStore.clear()
        oAuthStore.oAuthConsumer = null
        osmConnection.oAuth = null
        statisticsDao.clear()
        countryStatisticsDao.clear()
        userAchievementsDao.clear()
        userLinksDao.clear()
        userStore.clear()
        loginStatusListeners.forEach { it.onLoggedOut() }
    }

    fun updateUser() = launch(Dispatchers.IO) {
        try {
            val userDetails = userApi.mine

            userStore.setDetails(userDetails)
            val profileImageUrl = userDetails.profileImageUrl
            if (profileImageUrl != null) {
                updateAvatar(userDetails.id, profileImageUrl)
            }
            updateStatistics(userDetails.id)
        }
        catch (e: Exception) {
            Log.w(TAG, "Unable to download user details", e)
        }
    }

    private fun updateAvatar(userId: Long, imageUrl: String) = launch(Dispatchers.IO) {
        avatarsDownloader.download(userId, imageUrl)
        userAvatarListeners.forEach { it.onUserAvatarUpdated() }
    }

    private fun updateStatistics(userId: Long) = launch(Dispatchers.IO) {
        statisticsUpdater.updateFromBackend(userId)
    }

    override fun addLoginStatusListener(listener: UserLoginStatusListener) {
        loginStatusListeners.add(listener)
    }
    override fun removeLoginStatusListener(listener: UserLoginStatusListener) {
        loginStatusListeners.remove(listener)
    }
    override fun addUserAvatarListener(listener: UserAvatarListener) {
        userAvatarListeners.add(listener)
    }
    override fun removeUserAvatarListener(listener: UserAvatarListener) {
        userAvatarListeners.remove(listener)
    }

    companion object {
        const val TAG = "UserController"
    }
}

interface UserLoginStatusListener {
    fun onLoggedIn()
    fun onLoggedOut()
}

interface UserAvatarListener {
    fun onUserAvatarUpdated()
}

interface LoginStatusSource {
    val isLoggedIn: Boolean

    fun addLoginStatusListener(listener: UserLoginStatusListener)
    fun removeLoginStatusListener(listener: UserLoginStatusListener)
}

interface UserAvatarUpdateSource {
    fun addUserAvatarListener(listener: UserAvatarListener)
    fun removeUserAvatarListener(listener: UserAvatarListener)
}
