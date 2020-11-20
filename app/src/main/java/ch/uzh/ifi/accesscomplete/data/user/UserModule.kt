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
import dagger.Module
import dagger.Provides
import oauth.signpost.OAuthConsumer
import oauth.signpost.OAuthProvider
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import javax.inject.Named

@Module
object UserModule {
    // TODO sst: change values...
    private const val STATISTICS_BACKEND_URL = "https://www.westnordost.de/streetcomplete/statistics/"
    private const val BASE_OAUTH_URL = "https://www.openstreetmap.org/oauth/"
    private const val CONSUMER_KEY = "L3JyJMjVk6g5atwACVySRWgmnrkBAH7u0U18ALO7"
    private const val CONSUMER_SECRET = "uNjPaXZw15CPHdCSeMzttRm20tyFGaBPO7jHt52c"
    private const val CALLBACK_SCHEME = "streetcomplete"
    private const val CALLBACK_HOST = "oauth"

	@Provides fun statisticsDownloader(): StatisticsDownloader =
        StatisticsDownloader(STATISTICS_BACKEND_URL)

    @Provides fun oAuthStore(prefs: SharedPreferences): OAuthStore = OAuthStore(prefs) { defaultOAuthConsumer() }

    @Provides fun oAuthProvider(): OAuthProvider = DefaultOAuthProvider(
        BASE_OAUTH_URL + "request_token",
        BASE_OAUTH_URL + "access_token",
        BASE_OAUTH_URL + "authorize"
    )

	@Provides fun defaultOAuthConsumer(): OAuthConsumer =
        DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)

    @Provides fun loginStatusSource(userController: UserController): LoginStatusSource = userController

	@Provides @Named("OAuthCallbackScheme")
    fun oAuthCallbackScheme(): String = CALLBACK_SCHEME

	@Provides @Named("OAuthCallbackHost")
    fun oAuthCallbackHost(): String = CALLBACK_HOST
}
