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

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.core.content.getSystemService
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.quest.UnsyncedChangesCountListener
import ch.uzh.ifi.accesscomplete.data.quest.UnsyncedChangesCountSource
import ch.uzh.ifi.accesscomplete.data.upload.UploadController
import ch.uzh.ifi.accesscomplete.data.upload.UploadProgressListener
import ch.uzh.ifi.accesscomplete.data.user.UserController
import ch.uzh.ifi.accesscomplete.ktx.toast
import ch.uzh.ifi.accesscomplete.view.dialogs.RequestLoginDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Fragment that shows the upload button, including upload progress etc. */
class UploadButtonFragment : Fragment(R.layout.fragment_upload_button),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    @Inject internal lateinit var uploadController: UploadController
    @Inject internal lateinit var userController: UserController
    @Inject internal lateinit var unsyncedChangesCountSource: UnsyncedChangesCountSource
    @Inject internal lateinit var prefs: SharedPreferences

    private val uploadButton get() = view as UploadButton

    private val unsyncedChangesCountListener = object : UnsyncedChangesCountListener {
        override fun onUnsyncedChangesCountIncreased() { launch(Dispatchers.Main) { updateCount() }}
        override fun onUnsyncedChangesCountDecreased() { launch(Dispatchers.Main) { updateCount() }}
    }

    private val uploadProgressListener = object : UploadProgressListener {
        override fun onStarted() { launch(Dispatchers.Main) { updateProgress(true) } }
        override fun onFinished() { launch(Dispatchers.Main) { updateProgress(false) } }
    }

    /* --------------------------------------- Lifecycle ---------------------------------------- */

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadButton.setOnClickListener {
            if (isConnected()) {
                uploadChanges()
            } else {
                context?.toast(R.string.offline)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        /* Only show the button if autosync is off */
        uploadButton.isGone = isAutosync
        if (!isAutosync) {
            updateCount()
            updateProgress(uploadController.isUploadInProgress)
            uploadController.addUploadProgressListener(uploadProgressListener)
            unsyncedChangesCountSource.addListener(unsyncedChangesCountListener)
        }
    }

    override fun onStop() {
        super.onStop()
        uploadController.removeUploadProgressListener(uploadProgressListener)
        unsyncedChangesCountSource.removeListener(unsyncedChangesCountListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    // ---------------------------------------------------------------------------------------------

    private val isAutosync: Boolean get() =
        Prefs.Autosync.valueOf(prefs.getString(Prefs.AUTOSYNC, "ON")!!) == Prefs.Autosync.ON

    private fun updateCount() {
        uploadButton.uploadableCount = unsyncedChangesCountSource.count
    }

    private fun updateProgress(isUploadInProgress: Boolean) {
        if (isUploadInProgress) {
            uploadButton.isEnabled = false
            uploadButton.showProgress = true
        } else {
            uploadButton.isEnabled = true
            uploadButton.showProgress = false
        }
    }

    private fun uploadChanges() {
        // because the app should ask for permission even if there is nothing to upload right now
        if (!userController.isLoggedIn) {
            context?.let { RequestLoginDialog(it).show() }
        } else {
            uploadController.upload()
        }
    }

    /** Does not necessarily mean that the user has internet. But if he is not connected, he will
      * not have internet  */
    private fun isConnected(): Boolean {
        val connectivityManager = context?.getSystemService<ConnectivityManager>()
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
