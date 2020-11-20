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
import ch.uzh.ifi.accesscomplete.data.elementfilter.quote
import ch.uzh.ifi.accesscomplete.data.elementfilter.quoteIfNecessary

abstract class CompareTagValue(val key: String, val value: Float): ElementFilter {
    override fun toOverpassQLString() : String {
        val strVal = if (value - value.toInt() == 0f) value.toInt().toString() else value.toString()
        return "[" + key.quoteIfNecessary() + "](if: number(t[" + key.quote() + "]) " + operator + " " + strVal + ")"
    }

    override fun toString() = toOverpassQLString()

    override fun matches(obj: Element?): Boolean {
        val tagValue = obj?.tags?.get(key)?.toFloatOrNull() ?: return false
        return compareTo(tagValue)
    }

    abstract fun compareTo(tagValue: Float): Boolean
    abstract val operator: String
}
