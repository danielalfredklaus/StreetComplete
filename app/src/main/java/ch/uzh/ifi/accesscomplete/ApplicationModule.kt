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

package ch.uzh.ifi.accesscomplete

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import ch.uzh.ifi.accesscomplete.location.LocationRequestFragment
import ch.uzh.ifi.accesscomplete.util.CrashReportExceptionHandler
import ch.uzh.ifi.accesscomplete.util.SoundFx
import javax.inject.Singleton


@Module class ApplicationModule(private val application: Application) {

    @Provides fun appContext(): Context = application

    @Provides fun application(): Application = application

    @Provides fun preferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)

    @Provides fun assetManager(): AssetManager = application.assets

    @Provides fun resources(): Resources = application.resources

    @Provides fun locationRequestComponent(): LocationRequestFragment = LocationRequestFragment()

    @Provides @Singleton fun soundFx(): SoundFx = SoundFx(appContext())

    @Provides @Singleton fun exceptionHandler(ctx: Context): CrashReportExceptionHandler =
        CrashReportExceptionHandler(
            ctx,
            "sven.stoll@uzh.ch",
            "crashreport.txt"
        )
}
