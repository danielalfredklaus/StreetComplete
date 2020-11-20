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

package ch.uzh.ifi.accesscomplete.util

import android.net.Uri
import androidx.core.net.toUri
import java.util.*

fun parseGeoUri(uri: Uri): GeoLocation? {
    if (uri.scheme != "geo") return null

    val geoUriRegex = Regex("(-?[0-9]*\\.?[0-9]+),(-?[0-9]*\\.?[0-9]+).*?(?:\\?z=([0-9]*\\.?[0-9]+))?")
    val match = geoUriRegex.matchEntire(uri.schemeSpecificPart) ?: return null

    val latitude = match.groupValues[1].toDoubleOrNull() ?: return null
    if (latitude < -90 || latitude > +90) return null
    val longitude = match.groupValues[2].toDoubleOrNull() ?: return null
    if (longitude < -180 && longitude > +180) return null

    // zoom is optional. If it is invalid, we treat it the same as if it is not there
    val zoom = match.groupValues[3].toFloatOrNull()

    return GeoLocation(latitude, longitude, zoom)
}

fun buildGeoUri(latitude: Double, longitude: Double, zoom: Float? = null): Uri {
    val zoomStr = if (zoom != null) "?z=$zoom" else ""
    val geoUri = Formatter(Locale.US).format("geo:%.5f,%.5f%s", latitude, longitude, zoomStr).toString()
    return geoUri.toUri()
}

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val zoom: Float?
)
