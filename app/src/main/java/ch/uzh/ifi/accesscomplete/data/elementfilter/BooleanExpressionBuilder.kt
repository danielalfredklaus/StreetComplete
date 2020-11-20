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

/** Builds a boolean expression. Basically a BooleanExpression with a cursor.  */
class BooleanExpressionBuilder<I : Matcher<T>, T> {
    private var node: Chain<I, T> = BracketHelper()
    private var bracketCount = 0

    fun build(): BooleanExpression<I, T>? {
        if (bracketCount > 0) {
            throw IllegalStateException("Closed one bracket too little")
        }

        while (node.parent != null) {
            node = node.parent!!
        }

        node.flatten()

        // flatten cannot remove itself, but we wanna do that
        when(node.children.size) {
            0 -> return null
            1 -> {
                val firstChild = node.children.first()
                node.removeChild(firstChild)
                return firstChild
            }
        }

        node.ensureNoBracketNodes()
        return node
    }

    fun addOpenBracket() {
        val group = BracketHelper<I, T>()
        node.addChild(group)
        node = group

        bracketCount++
    }

    fun addCloseBracket() {
        if (--bracketCount < 0 ) throw IllegalStateException("Closed one bracket too much")

        while(node !is BracketHelper) {
            node = node.parent!!
        }
        node = node.parent!!
        node.flatten()
    }

    fun addValue(i: I) {
        node.addChild(Leaf(i))
    }

    fun addAnd() {
        if (node !is AllOf) {
            val last = node.children.last()
            val allOf = AllOf<I, T>()
            node.replaceChild(last, allOf)
            allOf.addChild(last)
            node = allOf
        }
    }

    fun addOr() {
        val allOf = node as? AllOf
        val group = node as? BracketHelper

        if (allOf != null) {
            val nodeParent = node.parent
            if (nodeParent is AnyOf) {
                node = nodeParent
            } else  {
                nodeParent?.removeChild(allOf)
                val anyOf = AnyOf<I, T>()
                anyOf.addChild(allOf)
                nodeParent?.addChild(anyOf)
                node = anyOf
            }
        }
        else if (group != null) {
            val last = node.children.last()
            val anyOf = AnyOf<I, T>()
            node.replaceChild(last, anyOf)
            anyOf.addChild(last)
            node = anyOf
        }
    }
}

private fun <I : Matcher<T>, T> Chain<I, T>.ensureNoBracketNodes() {
    if (this is BracketHelper) throw IllegalStateException("BooleanExpression still contains a Bracket node!")

    val it = children.listIterator()
    while (it.hasNext()) {
        val child = it.next()
        if (child is Chain) child.ensureNoBracketNodes()
    }
}

private class BracketHelper<I : Matcher<T>, T> : Chain<I, T>() {
    override fun matches(obj: T?) = throw IllegalStateException("Bracket cannot match")
}
