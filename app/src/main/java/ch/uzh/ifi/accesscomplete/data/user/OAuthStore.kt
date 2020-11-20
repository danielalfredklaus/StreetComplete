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
import ch.uzh.ifi.accesscomplete.Prefs
import oauth.signpost.OAuthConsumer
import javax.inject.Inject
import javax.inject.Provider

/** Manages saving and loading OAuthConsumer persistently  */
class OAuthStore @Inject constructor(
    private val prefs: SharedPreferences,
    private val oAuthConsumerProvider: Provider<OAuthConsumer>
) {
    var oAuthConsumer: OAuthConsumer?
        get() {
            val result = oAuthConsumerProvider.get()
            val accessToken = prefs.getString(Prefs.OAUTH_ACCESS_TOKEN, null)
            val accessTokenSecret = prefs.getString(Prefs.OAUTH_ACCESS_TOKEN_SECRET, null)
            if (accessToken == null || accessTokenSecret == null) return null
            result.setTokenWithSecret(accessToken, accessTokenSecret)
            return result
        }
        set(value) {
            if (value != null && value.token != null && value.tokenSecret != null) {
                prefs.edit {
                    putString(Prefs.OAUTH_ACCESS_TOKEN, value.token)
                    putString(Prefs.OAUTH_ACCESS_TOKEN_SECRET, value.tokenSecret)
                }
            } else {
                prefs.edit {
                    remove(Prefs.OAUTH_ACCESS_TOKEN)
                    remove(Prefs.OAUTH_ACCESS_TOKEN_SECRET)
                }
            }
        }

    val isAuthorized: Boolean
        get() = prefs.getString(Prefs.OAUTH_ACCESS_TOKEN_SECRET, null) != null
}
