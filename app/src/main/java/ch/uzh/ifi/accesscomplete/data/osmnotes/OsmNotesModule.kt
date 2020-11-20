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

import android.content.Context
import dagger.Module
import dagger.Provides
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import java.io.File
import javax.inject.Named

@Module
object OsmNotesModule {

    /* NOTE: most dependents don't actually let dagger inject this dependency but just use this
	   static method to initialize it themselves. This is not clean, but for some reason, having an
	   @Inject @Named("AvatarsCacheDirectory") internal lateinit var avatarsCacheDirectory: File
	   doesn't work. Dagger2 always reports that it does not know how to inject it.
	*/
    @Provides @Named("AvatarsCacheDirectory")
    fun getAvatarsCacheDirectory(context: Context): File {
        return File(context.cacheDir, ApplicationConstants.AVATARS_CACHE_DIRECTORY)
    }

    @Provides
    fun imageUploader(): StreetCompleteImageUploader =
        StreetCompleteImageUploader(ApplicationConstants.SC_PHOTO_SERVICE_URL)
}
