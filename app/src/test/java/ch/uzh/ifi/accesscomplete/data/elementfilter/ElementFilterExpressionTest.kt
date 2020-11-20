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

import org.junit.Test

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.any
import ch.uzh.ifi.accesscomplete.data.elementfilter.filters.ElementFilter
import ch.uzh.ifi.accesscomplete.mock
import ch.uzh.ifi.accesscomplete.on

import org.junit.Assert.*
import java.util.*

class ElementFilterExpressionTest {
    // Tests for toOverpassQLString are in FiltersParserTest

    private val node = createElement(Element.Type.NODE)
    private val way = createElement(Element.Type.WAY)
    private val relation = createElement(Element.Type.RELATION)

    @Test fun `matches nodes`() {
        val expr = createMatchExpression(ElementsTypeFilter.NODES)

        assertTrue(expr.matches(node))
        assertFalse(expr.matches(way))
        assertFalse(expr.matches(relation))
    }

    @Test fun `matches ways`() {
        val expr = createMatchExpression(ElementsTypeFilter.WAYS)

        assertFalse(expr.matches(node))
        assertTrue(expr.matches(way))
        assertFalse(expr.matches(relation))
    }

    @Test fun `matches relations`() {
        val expr = createMatchExpression(ElementsTypeFilter.RELATIONS)

        assertFalse(expr.matches(node))
        assertFalse(expr.matches(way))
        assertTrue(expr.matches(relation))
    }

    @Test fun `matches nwr`() {
        val expr = createMatchExpression(*ElementsTypeFilter.values())

        assertTrue(expr.matches(node))
        assertTrue(expr.matches(way))
        assertTrue(expr.matches(relation))
    }

    @Test fun `matches nw`() {
        val expr = createMatchExpression(ElementsTypeFilter.WAYS, ElementsTypeFilter.NODES)

        assertTrue(expr.matches(node))
        assertTrue(expr.matches(way))
    }

    @Test fun `matches wr`() {
        val expr = createMatchExpression(ElementsTypeFilter.WAYS, ElementsTypeFilter.RELATIONS)

        assertTrue(expr.matches(way))
        assertTrue(expr.matches(relation))
    }

    @Test fun `matches filter`() {
        val tagFilter: ElementFilter = mock()
        val expr = ElementFilterExpression(EnumSet.of(ElementsTypeFilter.NODES), Leaf(tagFilter))

        on(tagFilter.matches(any())).thenReturn(true)
        assertTrue(expr.matches(node))
        on(tagFilter.matches(any())).thenReturn(false)
        assertFalse(expr.matches(node))
    }

    private fun createElement(type: Element.Type): Element {
        val element: Element = mock()
        on(element.type).thenReturn(type)
        return element
    }

    private fun createMatchExpression(vararg elementsTypeFilter: ElementsTypeFilter): ElementFilterExpression {
        val tagFilter: ElementFilter = mock()
        on(tagFilter.matches(any())).thenReturn(true)
        return ElementFilterExpression(createEnumSet(*elementsTypeFilter), Leaf(tagFilter))
    }

    private fun createEnumSet(vararg filters: ElementsTypeFilter): EnumSet<ElementsTypeFilter> {
        return when (filters.size) {
            1 -> EnumSet.of(filters[0])
            2 -> EnumSet.of(filters[0], filters[1])
            3 -> EnumSet.of(filters[0], filters[1], filters[2])
            else -> throw IllegalStateException()
        }
    }
}
