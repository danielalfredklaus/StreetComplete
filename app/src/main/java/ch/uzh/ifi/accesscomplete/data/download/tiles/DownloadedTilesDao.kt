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

package ch.uzh.ifi.accesscomplete.data.download.tiles

import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesTable.Columns.DATE
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesTable.Columns.TYPE
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesTable.Columns.X
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesTable.Columns.Y
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesTable.NAME
import ch.uzh.ifi.accesscomplete.ktx.query
import ch.uzh.ifi.accesscomplete.util.Tile
import ch.uzh.ifi.accesscomplete.util.TilesRect
import javax.inject.Inject

/** Keeps info in which areas things have been downloaded already in a tile grid */
class DownloadedTilesDao @Inject constructor(private val dbHelper: SQLiteOpenHelper) {

    private val db get() = dbHelper.writableDatabase

    /** Persist that the given type has been downloaded in every tile in the given tile range  */
    fun put(tilesRect: TilesRect, typeName: String) {
        val time = System.currentTimeMillis()
        for (tile in tilesRect.asTileSequence()) {
            val values = contentValuesOf(
                X to tile.x,
                Y to tile.y,
                TYPE to typeName,
                DATE to time
            )
            db.replaceOrThrow(NAME, null, values)
        }
    }

    /** Invalidate all types within the given tile. (consider them as not-downloaded) */
    fun remove(tile: Tile): Int {
        return db.delete(NAME, "$X = ? AND $Y = ?", arrayOf(tile.x.toString(), tile.y.toString()))
    }

    fun removeAll() {
        db.execSQL("DELETE FROM $NAME")
    }

    /** @return a list of type names which have already been downloaded in every tile in the
     *  given tile range
     */
    fun get(tilesRect: TilesRect, ignoreOlderThan: Long): List<String> {
        val tileCount = tilesRect.size
        return db.query(NAME,
            columns = arrayOf(TYPE),
            selection = "$X BETWEEN ? AND ? AND $Y BETWEEN ? AND ? AND $DATE > ?",
            selectionArgs = arrayOf(
                tilesRect.left.toString(),
                tilesRect.right.toString(),
                tilesRect.top.toString(),
                tilesRect.bottom.toString(),
                ignoreOlderThan.toString()
            ),
            groupBy = TYPE,
            having = "COUNT(*) >= $tileCount") { it.getString(0) }
    }
}
