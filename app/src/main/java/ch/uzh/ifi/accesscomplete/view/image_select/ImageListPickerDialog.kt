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

package ch.uzh.ifi.accesscomplete.view.image_select

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.uzh.ifi.accesscomplete.R

/** A dialog in which you can one item of a range of items */
class ImageListPickerDialog<T>(
    context: Context,
    items: List<DisplayItem<T>>,
    cellLayoutId: Int = R.layout.cell_labeled_image_select,
    columns: Int = 2,
    onSelection: (DisplayItem<T>) -> Unit) : AlertDialog(context, R.style.Theme_Bubble_Dialog) {

    init {
        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        recyclerView.layoutManager = GridLayoutManager(context, columns)

        setTitle(R.string.quest_select_hint)
        setView(recyclerView)

        val adapter = ImageSelectAdapter<T>(1)
        adapter.cellLayoutId = cellLayoutId
        adapter.items = items
        adapter.listeners.add(object : ImageSelectAdapter.OnItemSelectionListener {
            override fun onIndexSelected(index: Int) {
                dismiss()
                onSelection(adapter.items[index])
            }

            override fun onIndexDeselected(index: Int) {}
        })
        recyclerView.adapter = adapter
    }
}
