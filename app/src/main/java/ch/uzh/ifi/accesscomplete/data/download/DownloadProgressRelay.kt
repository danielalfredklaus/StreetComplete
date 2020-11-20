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

package ch.uzh.ifi.accesscomplete.data.download

import java.util.concurrent.CopyOnWriteArrayList

class DownloadProgressRelay : DownloadProgressListener {

    private val listeners = CopyOnWriteArrayList<DownloadProgressListener>()

    override fun onStarted() { listeners.forEach { it.onStarted() } }
    override fun onError(e: Exception) { listeners.forEach { it.onError(e) } }
    override fun onSuccess() { listeners.forEach { it.onSuccess() } }
    override fun onFinished() { listeners.forEach { it.onFinished() } }
    override fun onStarted(item: DownloadItem) { listeners.forEach { it.onStarted(item) } }
    override fun onFinished(item: DownloadItem) {listeners.forEach { it.onFinished(item) } }

    fun addListener(listener: DownloadProgressListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: DownloadProgressListener) {
        listeners.remove(listener)
    }
}
