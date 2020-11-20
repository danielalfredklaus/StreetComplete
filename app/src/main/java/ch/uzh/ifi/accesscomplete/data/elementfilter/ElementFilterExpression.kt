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

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementsTypeFilter.NODES
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementsTypeFilter.WAYS
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementsTypeFilter.RELATIONS
import ch.uzh.ifi.accesscomplete.data.elementfilter.filters.ElementFilter
import java.util.*

/** Represents a parse result of a string in filter syntax, i.e.
 *  "ways with (highway = residential or highway = tertiary) and !name"  */
class ElementFilterExpression(
    private val elementsTypes: EnumSet<ElementsTypeFilter>,
    private val elementExprRoot: BooleanExpression<ElementFilter, Element>?
) {
    /** returns whether the given element is found through (=matches) this expression */
    fun matches(element: Element): Boolean =
        includesElementType(element.type) && (elementExprRoot?.matches(element) ?: true)

    fun includesElementType(elementType: Element.Type): Boolean = when (elementType) {
        Element.Type.NODE -> elementsTypes.contains(NODES)
        Element.Type.WAY -> elementsTypes.contains(WAYS)
        Element.Type.RELATION -> elementsTypes.contains(RELATIONS)
        else -> false
    }

    /** returns this expression as a Overpass query string */
    fun toOverpassQLString(): String = OverpassQueryCreator(elementsTypes, elementExprRoot).create()
}

/** Enum that specifies which type(s) of elements to retrieve  */
enum class ElementsTypeFilter { NODES, WAYS, RELATIONS }
