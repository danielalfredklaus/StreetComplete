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
import android.database.sqlite.SQLiteDatabase
import ch.uzh.ifi.accesscomplete.data.WhereSelectionBuilder

/**
 * Run [body] in a transaction marking it as successful if it completes without exception.
 */
inline fun <T> SQLiteDatabase.transaction(body: SQLiteDatabase.() -> T): T {
    beginTransaction()
    try {
        val result = body()
        setTransactionSuccessful()
        return result
    } finally {
        endTransaction()
    }
}


fun <R> SQLiteDatabase.query(
    table: String,
    columns: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    groupBy: String? = null,
    having: String? = null,
    orderBy: String? = null,
    transform: (Cursor) -> R
): List<R> = query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null).use { cursor ->
    val result = ArrayList<R>(cursor.count)
    cursor.moveToFirst()
    while(!cursor.isAfterLast) {
        result.add(transform(cursor))
        cursor.moveToNext()
    }
    result
}

fun <R> SQLiteDatabase.queryOne(
    table: String,
    columns: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    orderBy: String? = null,
    transform: (Cursor) -> R?
): R? = query(table, columns, selection, selectionArgs, null, null, orderBy, "1").use { cursor ->
    if (cursor.moveToFirst()) transform(cursor) else null
}

fun <R> SQLiteDatabase.query(
    table: String,
    columns: Array<String>? = null,
    selection: WhereSelectionBuilder? = null,
    groupBy: String? = null,
    having: String? = null,
    orderBy: String? = null,
    transform: (Cursor) -> R
): List<R> = query(table, columns, selection?.where, selection?.args, groupBy, having, orderBy, transform)


fun <R> SQLiteDatabase.queryOne(
    table: String,
    columns: Array<String>? = null,
    selection: WhereSelectionBuilder? = null,
    orderBy: String? = null,
    transform: (Cursor) -> R
): R? = queryOne(table, columns, selection?.where, selection?.args, orderBy, transform)


fun SQLiteDatabase.hasColumn(tableName: String, columnName: String): Boolean {
    return queryOne(
        "pragma_table_info('$tableName')",
        arrayOf("name"),
        "name = ?",
        arrayOf(columnName)) { it.getString(0) } != null
}
