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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import javax.inject.Inject
import javax.inject.Singleton

/** Controls uploading */
@Singleton class UploadController @Inject constructor(
    private val context: Context
): UploadProgressSource {

    private var uploadServiceIsBound = false
    private var uploadService: UploadService.Interface? = null
    private val uploadServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            uploadService = service as UploadService.Interface
            uploadService?.setProgressListener(uploadProgressRelay)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            uploadService = null
        }
    }
    private val uploadProgressRelay = UploadProgressRelay()

    override val isUploadInProgress: Boolean get() =
        uploadService?.isUploadInProgress == true

    init {
        bindServices()
    }

    /** Collect and upload all changes made by the user  */
    fun upload() {
        context.startService(UploadService.createIntent(context))
    }

    private fun bindServices() {
        uploadServiceIsBound = context.bindService(
            Intent(context, UploadService::class.java),
            uploadServiceConnection, Context.BIND_AUTO_CREATE
        )
    }

    private fun unbindServices() {
        if (uploadServiceIsBound) context.unbindService(uploadServiceConnection)
        uploadServiceIsBound = false
    }

    override fun addUploadProgressListener(listener: UploadProgressListener) {
        uploadProgressRelay.addListener(listener)
    }
    override fun removeUploadProgressListener(listener: UploadProgressListener) {
        uploadProgressRelay.removeListener(listener)
    }
}
