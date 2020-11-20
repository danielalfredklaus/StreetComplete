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

package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.meta.getLastCheckDateKeys
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDate
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDateString
import java.util.*

abstract class CompareTagAge(val key: String, val dateFilter: DateFilter) : ElementFilter {
    val date: Date get() = dateFilter.date

    override fun toOverpassQLString(): String {
        val dateStr = date.toCheckDateString()
        val datesToCheck = (listOf("timestamp()") + getLastCheckDateKeys(key).map { "t['$it']" })
        val oqlEvaluators = datesToCheck.joinToString(" || ") { "date($it) $operator date('$dateStr')" }
        return "(if: $oqlEvaluators)"
    }

    override fun toString() = toOverpassQLString()

    override fun matches(obj: Element?): Boolean {
        val dateElementEdited = obj?.dateEdited ?: return false

        if (compareTo(dateElementEdited)) return true

        return getLastCheckDateKeys(key)
            .mapNotNull { obj.tags[it]?.toCheckDate() }
            .any { compareTo(it) }
    }

    abstract fun compareTo(tagValue: Date): Boolean
    abstract val operator: String
}
