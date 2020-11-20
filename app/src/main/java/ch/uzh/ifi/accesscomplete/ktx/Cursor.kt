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

package ch.uzh.ifi.accesscomplete.ktx

import android.database.Cursor
import androidx.core.database.*

fun Cursor.getLong(columnName: String): Long = getLong(getColumnIndexOrThrow(columnName))
fun Cursor.getInt(columnName: String): Int = getInt(getColumnIndexOrThrow(columnName))
fun Cursor.getShort(columnName: String): Short = getShort(getColumnIndexOrThrow(columnName))
fun Cursor.getDouble(columnName: String): Double = getDouble(getColumnIndexOrThrow(columnName))
fun Cursor.getFloat(columnName: String): Float = getFloat(getColumnIndexOrThrow(columnName))
fun Cursor.getString(columnName: String): String = getString(getColumnIndexOrThrow(columnName))
fun Cursor.getBlob(columnName: String): ByteArray = getBlob(getColumnIndexOrThrow(columnName))

fun Cursor.getLongOrNull(columnName: String): Long? = getLongOrNull(getColumnIndexOrThrow(columnName))
fun Cursor.getIntOrNull(columnName: String): Int? = getIntOrNull(getColumnIndexOrThrow(columnName))
fun Cursor.getShortOrNull(columnName: String): Short? = getShortOrNull(getColumnIndexOrThrow(columnName))
fun Cursor.getDoubleOrNull(columnName: String): Double? = getDoubleOrNull(getColumnIndexOrThrow(columnName))
fun Cursor.getFloatOrNull(columnName: String): Float? = getFloatOrNull(getColumnIndexOrThrow(columnName))
fun Cursor.getStringOrNull(columnName: String): String? = getStringOrNull(getColumnIndexOrThrow(columnName))
fun Cursor.getBlobOrNull(columnName: String): ByteArray? = getBlobOrNull(getColumnIndexOrThrow(columnName))

inline fun <reified T> Cursor.get(columnName: String): T {
    val index = getColumnIndexOrThrow(columnName)
    return when(T::class) {
        Long::class -> getLong(index)
        Int::class -> getInt(index)
        Short::class -> getShort(index)
        Double::class -> getDouble(index)
        Float::class -> getFloat(index)
        String::class -> getString(index)
        ByteArray::class -> getBlob(index)
        else -> throw ClassCastException("Expected either an Int, Short, Long, Float, Double, String or ByteArray")
    } as T
}
