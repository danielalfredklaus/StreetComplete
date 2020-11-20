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

package ch.uzh.ifi.accesscomplete.data.elementfilter

import de.westnordost.osmapi.map.data.OsmNode
import ch.uzh.ifi.accesscomplete.data.elementfilter.filters.ElementFilter

import java.text.SimpleDateFormat
import java.util.*

/** Returns the date x days in the past */
fun dateDaysAgo(daysAgo: Float): Date {
    val cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.SECOND, -(daysAgo * 24 * 60 * 60).toInt())
    return cal.time
}

fun ElementFilter.matches(tags: Map<String,String>, date: Date? = null): Boolean =
    matches(OsmNode(1, 1, 0.0, 0.0, tags, null, date))

val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
