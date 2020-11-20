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
import ch.uzh.ifi.accesscomplete.data.elementfilter.ElementsTypeFilter.*
import ch.uzh.ifi.accesscomplete.data.elementfilter.filters.ElementFilter
import java.util.*

/** Create an overpass query from the given element filter expression */
class OverpassQueryCreator(
    elementTypes: EnumSet<ElementsTypeFilter>,
    private val expr: BooleanExpression<ElementFilter, Element>?)
{
    private val elementTypes = elementTypes.toOqlNames()
    private var setIdCounter: Int = 1
    private val dataSets: MutableMap<BooleanExpression<ElementFilter, Element>, Int> = mutableMapOf()

    fun create(): String {
         if (elementTypes.size == 1) {
            val elementType = elementTypes.first()
            if (expr == null) {
                return "$elementType;\n"
            }
             return expr.toOverpassString(elementType, null)
        } else {
            if (expr == null) {
                return "(" + elementTypes.joinToString(" ") { "$it; " } + ");\n"
            }

            val result = StringBuilder()
            val resultSetId = expr.assignResultSetId()
            for (elementType in elementTypes) {
                result.append(expr.toOverpassString(elementType, resultSetId))
            }
            val unionChildren = elementTypes.joinToString(" ") { getSetId(it, resultSetId) + ";" }
            result.append("($unionChildren);\n")
            return result.toString()
        }
    }

    private fun EnumSet<ElementsTypeFilter>.toOqlNames(): List<String> = when {
        containsAll(listOf(NODES, WAYS, RELATIONS)) ->  listOf("nwr")
        containsAll(listOf(NODES, WAYS)) ->             listOf("nw")
        containsAll(listOf(WAYS, RELATIONS)) ->         listOf("wr")
        else -> map { when (it!!) {
            NODES -> "node"
            WAYS -> "way"
            RELATIONS -> "rel"
        } }
    }

    private fun BooleanExpression<ElementFilter, Element>.toOverpassString(elementType: String, resultSetId: Int?): String {
        return when (this) {
            is Leaf -> AllTagFilters(value).toOverpassString(elementType, null, resultSetId)
            is AnyOf -> toOverpassString(elementType, null, resultSetId)
            is AllOf -> toOverpassString(elementType, null, resultSetId)
            else -> throw IllegalStateException("Unexpected expression")
        }
    }

    private fun AllOf<ElementFilter, Element>.childrenWithLeavesMerged(): List<BooleanExpression<ElementFilter, Element>> {
        val consecutiveLeaves = mutableListOf<ElementFilter>()
        val mergedChildren = mutableListOf<BooleanExpression<ElementFilter, Element>>()
        for (child in children) {
            when (child) {
                is Leaf -> consecutiveLeaves.add(child.value)
                is AnyOf -> {
                    if (consecutiveLeaves.isNotEmpty()) {
                        mergedChildren.add(AllTagFilters(consecutiveLeaves.toList()))
                        consecutiveLeaves.clear()
                    }
                    mergedChildren.add(child)
                }
                else -> throw IllegalStateException("Expected only Leaf and AnyOf children")
            }
        }
        if (consecutiveLeaves.isNotEmpty()) {
            mergedChildren.add(AllTagFilters(consecutiveLeaves.toList()))
        }
        return mergedChildren
    }

    private fun AllOf<ElementFilter, Element>.toOverpassString(elementType: String, inputSetId: Int?, resultSetId: Int?): String {
        val result = StringBuilder()
        val workingSet by lazy { assignResultSetId() }

        val childrenMerged = childrenWithLeavesMerged()
        childrenMerged.forEachIndexed { i, child ->
            val isFirst = i == 0
            val isLast = i == childrenMerged.lastIndex
            val stmtInputSetId = if (isFirst) inputSetId else workingSet
            val stmtResultSetId = if (isLast) resultSetId else workingSet

            if (child is AnyOf) result.append(child.toOverpassString(elementType, stmtInputSetId, stmtResultSetId))
            else if (child is AllTagFilters) result.append(child.toOverpassString(elementType, stmtInputSetId, stmtResultSetId))
        }
        return result.toString()
    }

    private fun AnyOf<ElementFilter, Element>.toOverpassString(elementType: String, inputSetId: Int?, resultSetId: Int?): String {
        val childrenResultSetIds = mutableListOf<Int>()
        val result = StringBuilder()
        // first print every nested statement
        for (child in children) {
            val workingSetId = child.assignResultSetId()
            result.append(when (child) {
                is Leaf ->
                    AllTagFilters(child.value).toOverpassString(elementType, inputSetId, workingSetId)
                is AllOf ->
                    child.toOverpassString(elementType, inputSetId, workingSetId)
                else ->
                    throw IllegalStateException("Expected only Leaf and AllOf children")
            })
            childrenResultSetIds.add(workingSetId)
        }
        // then union all direct children
        val unionChildren = childrenResultSetIds.joinToString(" ") { getSetId(elementType, it)+";" }
        val resultStmt = resultSetId?.let { " -> " + getSetId(elementType,it) }.orEmpty()
        result.append("($unionChildren)$resultStmt;\n")
        return result.toString()
    }

    private fun AllTagFilters.toOverpassString(elementType: String, inputSetId: Int?, resultSetId: Int?): String {
        val elementFilter = elementType + inputSetId?.let { getSetId(elementType,it) }.orEmpty()
        val tagFilters = values.joinToString("") { it.toOverpassQLString() }
        val resultStmt = resultSetId?.let { " -> " + getSetId(elementType,it) }.orEmpty()
        return "$elementFilter$tagFilters$resultStmt;\n"
    }

    private fun getSetId(elementType: String, id: Int): String {
        val prefix = when (elementType) {
            "node" -> "n"
            "way" -> "w"
            "rel" -> "r"
            "nwr", "nw", "wr" -> "e"
            else -> throw IllegalArgumentException("Expected element to be any of 'node', 'way', 'rel', 'nw', 'wr' or 'nwr'")
        }
        return ".$prefix$id"
    }

    private fun BooleanExpression<ElementFilter, Element>.assignResultSetId(): Int {
        return dataSets.getOrPut(this) { setIdCounter++ }
    }

    private class AllTagFilters(val values: List<ElementFilter>) : BooleanExpression<ElementFilter, Element>() {
        constructor(value: ElementFilter) : this(listOf(value))
        override fun matches(obj: Element?) = values.all { it.matches(obj) }
        override fun toString() = values.joinToString(" and ")
    }
}
