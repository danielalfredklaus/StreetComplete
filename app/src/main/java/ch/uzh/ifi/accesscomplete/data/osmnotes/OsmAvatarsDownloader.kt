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

package ch.uzh.ifi.accesscomplete.data.osmnotes

import android.util.Log
import ch.uzh.ifi.accesscomplete.data.UserApi
import ch.uzh.ifi.accesscomplete.ktx.saveToFile
import java.io.File
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import javax.inject.Named

/** Downloads and stores the OSM avatars of users */
class OsmAvatarsDownloader @Inject constructor(
    private val userApi: UserApi,
    @Named("AvatarsCacheDirectory") private val cacheDir: File
) {

    fun download(userIds: Collection<Long>) {
        if (!ensureCacheDirExists()) {
            Log.w(TAG, "Unable to create directories for avatars")
            return
        }

        for (userId in userIds) {
            val avatarUrl = userApi.get(userId)?.profileImageUrl
            if (avatarUrl != null) {
                download(userId, avatarUrl)
            }
        }
    }

    /** download avatar for the given user and a known avatar url */
    fun download(userId: Long, avatarUrl: String) {
        if (!ensureCacheDirExists()) return
        try {
            val avatarFile = File(cacheDir, "$userId")
            URL(avatarUrl).saveToFile(avatarFile)
            Log.i(TAG, "Saved file: ${avatarFile.path}")
        } catch (e: IOException) {
            Log.w(TAG, "Unable to download avatar for user id $userId")
        }
    }

    private fun ensureCacheDirExists(): Boolean {
        return cacheDir.exists() || cacheDir.mkdirs()
    }

    companion object {
        private const val TAG = "OsmAvatarsDownload"
    }
}
