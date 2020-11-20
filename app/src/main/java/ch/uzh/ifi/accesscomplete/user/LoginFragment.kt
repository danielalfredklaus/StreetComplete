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

package ch.uzh.ifi.accesscomplete.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import de.westnordost.osmapi.user.Permission
import ch.uzh.ifi.accesscomplete.BackPressedListener
import ch.uzh.ifi.accesscomplete.HasTitle
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.OsmApiModule
import ch.uzh.ifi.accesscomplete.data.PermissionsApi
import ch.uzh.ifi.accesscomplete.data.quest.UnsyncedChangesCountSource
import ch.uzh.ifi.accesscomplete.data.user.UserController
import ch.uzh.ifi.accesscomplete.ktx.childFragmentManagerOrNull
import ch.uzh.ifi.accesscomplete.ktx.toast
import ch.uzh.ifi.accesscomplete.settings.OAuthFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import oauth.signpost.OAuthConsumer
import javax.inject.Inject

/** Shows only a login button and a text that clarifies that login is necessary for publishing the
 *  answers. */
class LoginFragment : Fragment(R.layout.fragment_login),
    CoroutineScope by CoroutineScope(Dispatchers.Main),
    HasTitle,
    BackPressedListener,
    OAuthFragment.Listener {

    @Inject internal lateinit var unsyncedChangesCountSource: UnsyncedChangesCountSource
    @Inject internal lateinit var userController: UserController

    override val title: String get() = getString(R.string.user_login)

    private val oAuthFragment: OAuthFragment? get() =
        childFragmentManagerOrNull?.findFragmentById(R.id.oauthFragmentContainer) as? OAuthFragment

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginButton.setOnClickListener { pushOAuthFragment() }

        val launchAuth = arguments?.getBoolean(ARG_LAUNCH_AUTH, false) ?: false
        if (launchAuth) {
            pushOAuthFragment()
        }
    }

    override fun onStart() {
        super.onStart()

        val unsyncedChanges = unsyncedChangesCountSource.count
        unpublishedQuestsText.text = getString(R.string.unsynced_quests_not_logged_in_description, unsyncedChanges)
        unpublishedQuestsText.isGone = unsyncedChanges <= 0
    }

    override fun onBackPressed(): Boolean {
        val f = oAuthFragment
        if (f != null) {
            if(f.onBackPressed()) return true
            childFragmentManager.popBackStack("oauth", POP_BACK_STACK_INCLUSIVE)
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    /* ------------------------------- OAuthFragment.Listener ----------------------------------- */

    override fun onOAuthSuccess(consumer: OAuthConsumer) {
        loginButton.visibility = View.INVISIBLE
        loginProgress.visibility = View.VISIBLE
        childFragmentManager.popBackStack("oauth", POP_BACK_STACK_INCLUSIVE)
        launch {
            if (hasRequiredPermissions(consumer)) {
                userController.logIn(consumer)
            } else {
                context?.toast(R.string.oauth_failed_permissions, Toast.LENGTH_LONG)
                loginButton.visibility = View.VISIBLE
            }
            loginProgress.visibility = View.INVISIBLE
        }
    }

    override fun onOAuthFailed(e: Exception?) {
        childFragmentManager.popBackStack("oauth", POP_BACK_STACK_INCLUSIVE)
        userController.logOut()
    }

    suspend fun hasRequiredPermissions(consumer: OAuthConsumer): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val permissionsApi = PermissionsApi( OsmApiModule.osmConnection(consumer))
                permissionsApi.get().containsAll(REQUIRED_OSM_PERMISSIONS)
            }
            catch (e: Exception) { false }
            }
    }

    /* ------------------------------------------------------------------------------------------ */

    private fun pushOAuthFragment() {
        childFragmentManager.commit {
            setCustomAnimations(
                R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right
            )
            replace<OAuthFragment>(R.id.oauthFragmentContainer)
            addToBackStack("oauth")
        }
    }

    companion object {
        fun create(launchAuth: Boolean = false): LoginFragment {
            val f = LoginFragment()
            f.arguments = bundleOf(ARG_LAUNCH_AUTH to launchAuth)
            return f
        }

        private val REQUIRED_OSM_PERMISSIONS = listOf(
            Permission.READ_PREFERENCES_AND_USER_DETAILS,
            Permission.MODIFY_MAP,
            Permission.WRITE_NOTES
        )

        private const val ARG_LAUNCH_AUTH = "launch_auth"
    }
}
