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

package ch.uzh.ifi.accesscomplete.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/** Item decoration that adds a spacing between the items for RecyclerView that uses a GridLayoutManager*/
class GridLayoutSpacingItemDecoration(private val spacingInPx: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter?.itemCount ?: 0
        val layoutManager = parent.layoutManager as GridLayoutManager

        val spanCount = layoutManager.spanCount
        val spanSizeLookup = layoutManager.spanSizeLookup

        val row = spanSizeLookup.getSpanGroupIndex(position, spanCount)
        val span = spanSizeLookup.getSpanIndex(position, spanCount)
        val rowCount = spanSizeLookup.getSpanGroupIndex(count - 1, spanCount) + 1

        outRect.left = if (span > 0) spacingInPx/2 else 0
        outRect.right = if (span < spanCount - 1) spacingInPx/2 else 0
        outRect.top = if (row > 0) spacingInPx/2 else 0
        outRect.bottom = if (row < rowCount - 1) spacingInPx/2 else 0
    }
}
