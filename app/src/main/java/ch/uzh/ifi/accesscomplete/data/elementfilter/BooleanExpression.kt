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

import java.util.LinkedList

abstract class BooleanExpression<I : Matcher<T>,T> {
    var parent: Chain<I, T>? = null
        internal set

    abstract fun matches(obj: T?): Boolean
}

abstract class Chain<I : Matcher<T>, T> : BooleanExpression<I, T>() {
    protected val nodes: LinkedList<BooleanExpression<I, T>> = LinkedList()

    val children: List<BooleanExpression<I, T>> get() = nodes.toList()

    fun addChild(child: BooleanExpression<I, T>) {
        child.parent = this
        nodes.add(child)
    }

    fun removeChild(child: BooleanExpression<I, T>) {
        nodes.remove(child)
        child.parent = null
    }

    fun replaceChild(replace: BooleanExpression<I,T>, with: BooleanExpression<I,T>) {
        val it = nodes.listIterator()
        while (it.hasNext()) {
            val child = it.next()
            if (child === replace) {
                replaceChildAt(it, with)
                return
            }
        }
    }

    private fun replaceChildAt(
        at: MutableListIterator<BooleanExpression<I,T>>,
        vararg with: BooleanExpression<I,T>
    ) {
        at.remove()
        for (w in with) {
            at.add(w)
            w.parent = this
        }
    }

    /** Removes unnecessary depth in the expression tree  */
    fun flatten() {
        removeEmptyNodes()
        mergeNodesWithSameOperator()
    }

    /** remove nodes from superfluous brackets  */
    private fun removeEmptyNodes() {
        val it = nodes.listIterator()
        while (it.hasNext()) {
            val child = it.next() as? Chain ?: continue
            if (child.nodes.size == 1) {
                replaceChildAt(it, child.nodes.first)
                it.previous() // = the just replaced node will be checked again
            } else {
                child.removeEmptyNodes()
            }
        }
    }

    /** merge children recursively which do have the same operator set (and, or)  */
    private fun mergeNodesWithSameOperator() {
        val it = nodes.listIterator()
        while (it.hasNext()) {
            val child = it.next() as? Chain ?: continue
            child.mergeNodesWithSameOperator()

            // merge two successive nodes of same type
            if (child::class == this::class) {
                replaceChildAt(it, *child.children.toTypedArray())
            }
        }
    }
}

class Leaf<I : Matcher<T>, T>(val value: I) : BooleanExpression<I, T>() {
    override fun matches(obj: T?) = value.matches(obj)
    override fun toString() = value.toString()
}

class AllOf<I : Matcher<T>, T> : Chain<I, T>() {
    override fun matches(obj: T?) = nodes.all { it.matches(obj) }
    override fun toString() = nodes.joinToString(" and ") { if (it is AnyOf) "($it)" else "$it" }
}

class AnyOf<I : Matcher<T>, T> : Chain<I, T>() {
    override fun matches(obj: T?) = nodes.any { it.matches(obj) }
    override fun toString() = nodes.joinToString(" or ") { "$it" }
}

interface Matcher<in T> {
    fun matches(obj: T?): Boolean
}
