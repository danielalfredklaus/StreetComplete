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

package ch.uzh.ifi.accesscomplete.data

import ch.uzh.ifi.osmapi.map.LightweightOsmMapDataFactory
import dagger.Module
import dagger.Provides
import de.westnordost.osmapi.OsmConnection
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.data.user.OAuthStore
import oauth.signpost.OAuthConsumer
import javax.inject.Singleton

@Module
object OsmApiModule {

    private const val OSM_API_URL = "https://api.openstreetmap.org/api/0.6/"

    /** Returns the osm connection singleton used for all daos with the saved oauth consumer  */
    @Provides @Singleton fun osmConnection(oAuthStore: OAuthStore): OsmConnection {
        return osmConnection(oAuthStore.oAuthConsumer)
    }

    /** Returns an osm connection with the supplied consumer (note the difference to the above function)  */
    fun osmConnection(consumer: OAuthConsumer?): OsmConnection {
        return OsmConnection(OSM_API_URL, ApplicationConstants.USER_AGENT, consumer)
    }

    @Provides fun userDao(osm: OsmConnection): UserApi = UserApi(osm)

    @Provides fun notesDao(osm: OsmConnection): NotesApi = NotesApi(osm)

    @Provides fun mapDataDao(osm: OsmConnection): MapDataApi {
        // generally we are not interested in certain data returned by the OSM API, so we use a
        // map data factory that does not include that data
        return MapDataApi(osm, LightweightOsmMapDataFactory())
    }
}
