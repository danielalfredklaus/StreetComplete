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

package ch.uzh.ifi.accesscomplete.data.upload

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

/** Asks remote server if this version of the app is banned */
class VersionIsBannedChecker @Inject constructor(private val url: String, private val userAgent: String) {

    fun get(): BannedInfo {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(url).openConnection() as HttpURLConnection)
            connection.inputStream.bufferedReader().use { reader ->
                for (line in reader.lineSequence()) {
                    val text = line.split("\t".toRegex())
                    if (text[0] == userAgent) {
                        return IsBanned(if (text.size > 1) text[1] else null)
                    }
                }
            }
        } catch (e: IOException) {
            // if there is an io exception, never mind then...! (The unreachability of the above
            // internet address should not lead to this app being unusable!)
        } finally {
            connection?.disconnect()
        }
        return IsNotBanned
    }
}

class VersionBannedException(val banReason: String?)
    : RuntimeException("This version is banned from making any changes!")

sealed class BannedInfo

data class IsBanned(val reason: String?): BannedInfo()
object IsNotBanned : BannedInfo()
