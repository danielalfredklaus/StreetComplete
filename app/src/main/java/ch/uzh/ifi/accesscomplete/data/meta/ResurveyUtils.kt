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

package ch.uzh.ifi.accesscomplete.data.meta

import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.MILLISECOND

/** Returns all the known keys used for recording the date at which the tag with the given key
 *  should be checked again. */
fun getLastCheckDateKeys(key: String): Sequence<String> = sequenceOf(
    "$key:check_date", "check_date:$key",
    "$key:lastcheck", "lastcheck:$key",
    "$key:last_checked", "last_checked:$key"
)

fun Date.toCheckDateString(): String = OSM_CHECK_DATE_FORMAT.format(this)
fun String.toCheckDate(): Date? {
    val groups = OSM_CHECK_DATE_REGEX.matchEntire(this)?.groupValues ?: return null
    val year = groups[1].toIntOrNull() ?: return null
    val month = groups[2].toIntOrNull() ?: return null
    val day = groups[3].toIntOrNull() ?: 1

    val calendar = Calendar.getInstance()
    return try {
        // -1 because this is the month index
        calendar.set(year, month-1, day, 0, 0, 0)
        calendar.set(MILLISECOND, 0)
        calendar.time
    } catch (e: Exception) {
        null
    }
}

/** adds or modifies the given tag. If the updated tag is the same as before, sets the check date
 *  tag to today instead. */
fun StringMapChangesBuilder.updateWithCheckDate(key: String, value: String) {
    val previousValue = getPreviousValue(key)
    if (previousValue == value) {
        updateCheckDateForKey(key)
    } else {
        addOrModify(key, value)
        deleteCheckDatesForKey(key)
    }
}

/** Set/update solely the check date to today for the given key */
fun StringMapChangesBuilder.updateCheckDateForKey(key: String) {
    addOrModify("$SURVEY_MARK_KEY:$key", Date().toCheckDateString())
    // remove old check date keys (except the one we want to set)
    getLastCheckDateKeys(key).forEach {
        if (it != "$SURVEY_MARK_KEY:$key") deleteIfExists(it)
    }
}

/** Delete any check date keys for the given key */
fun StringMapChangesBuilder.deleteCheckDatesForKey(key: String) {
    getLastCheckDateKeys(key).forEach { deleteIfExists(it) }
}

/** Date format of the tags used for recording the date at which the element or tag with the given
 *  key should be checked again. */
private val OSM_CHECK_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
// not using date format because we want to be able to understand 2000-11 as well
private val OSM_CHECK_DATE_REGEX = Regex("([0-9]{4})-([0-9]{2})(?:-([0-9]{2}))?")
