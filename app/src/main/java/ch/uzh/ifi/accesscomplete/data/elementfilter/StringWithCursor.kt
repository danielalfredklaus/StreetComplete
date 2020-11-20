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

import kotlin.math.min

/** Convenience class to make it easier to go step by step through a string  */
class StringWithCursor(private val string: String) {
    var cursorPos = 0
        private set

    private val char: Char?
        get() = if (cursorPos < string.length) string[cursorPos] else null

    /** Advances the cursor if str is the next thing at the cursor.
     *  Returns whether the next string was the str */
    fun nextIsAndAdvance(str: String): Boolean {
        if (!nextIs(str)) return false
        advanceBy(str.length)
        return true
    }

    fun nextIsAndAdvance(c: Char): Boolean {
        if (!nextIs(c)) return false
        advance()
        return true
    }

    /** Advances the cursor if str or str.toUpperCase() is the next thing at the cursor
     *
     *  Returns whether the next string was the str or str.toUpperCase
     */
    fun nextIsAndAdvanceIgnoreCase(str: String): Boolean {
        if (!nextIsIgnoreCase(str)) return false
        advanceBy(str.length)
        return true
    }

    /** returns whether the cursor reached the end */
    fun isAtEnd(x: Int = 0): Boolean = cursorPos + x >= string.length
    fun findNext(str: String): Int = toDelta(string.indexOf(str, cursorPos))
    fun findNext(c: Char, offs: Int = 0): Int = toDelta(string.indexOf(c, cursorPos + offs))

    /** Advance cursor by one and return the character that was at that position
     *
     * throws IndexOutOfBoundsException if cursor is already at the end
     */
    fun advance(): Char {
        val result = string[cursorPos]
        cursorPos = min(string.length, cursorPos + 1)
        return result
    }

    /** Advance cursor by x and return the string that inbetween the two positions.
     * If cursor+x is beyond the end of the string, the method will just return the string until
     * the end of the string
     *
     * throws IndexOutOfBoundsException if x < 0
     */
    fun advanceBy(x: Int): String {
        val end = cursorPos + x
        val result: String
        if (string.length < end) {
            result = string.substring(cursorPos)
            cursorPos = string.length
        } else {
            result = string.substring(cursorPos, end)
            cursorPos = end
        }
        return result
    }

    fun previousIs(c: Char): Boolean = c == string[cursorPos - 1]
    fun nextIs(c: Char): Boolean = c == char
    fun nextIs(str: String): Boolean = string.startsWith(str, cursorPos)
    fun nextIsIgnoreCase(str: String): Boolean =
        nextIs(str.toLowerCase()) || nextIs(str.toUpperCase())

    fun nextMatches(regex: Regex): MatchResult? {
        val match = regex.find(string, cursorPos) ?: return null
        if (match.range.first != cursorPos) return null
        return match
    }

    fun nextMatchesAndAdvance(regex: Regex): MatchResult? {
        val result = nextMatches(regex) ?: return null
        advanceBy(result.value.length)
        return result
    }

    private fun toDelta(index: Int): Int =
        if (index == -1) string.length - cursorPos else index - cursorPos

    // good for debugging
    override fun toString(): String =
        string.substring(0, cursorPos) + "►" + string.substring(cursorPos)
}
