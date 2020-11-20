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

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import ch.uzh.ifi.accesscomplete.R
import java.util.concurrent.CopyOnWriteArrayList

/** Select a number of items from a list of items  */
class ImageSelectAdapter<T>(private val maxSelectableIndices: Int = -1) :
    RecyclerView.Adapter<ItemViewHolder>() {

    var items = listOf<DisplayItem<T>>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    private val _selectedIndices = mutableSetOf<Int>()
    val selectedIndices get() = _selectedIndices.toList()

    var cellLayoutId = R.layout.cell_labeled_image_select

    val listeners: MutableList<OnItemSelectionListener> = CopyOnWriteArrayList()

    val selectedItems get() = _selectedIndices.map { i -> items[i].value!! }

    interface OnItemSelectionListener {
        fun onIndexSelected(index: Int)
        fun onIndexDeselected(index: Int)
    }

    fun indexOf(item: T): Int = items.indexOfFirst { it.value == item }

    fun select(indices: List<Int>) {
        for (index in indices) {
            select(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(cellLayoutId, parent, false)
        val holder = ItemViewHolder(view)
        holder.onClickListener = ::toggle
        return holder
    }

    fun isSelected(index: Int) = _selectedIndices.contains(index)

    fun select(index: Int) {
        checkIndexRange(index)
        // special case: toggle-behavior if only one index can be selected
        if (maxSelectableIndices == 1 && _selectedIndices.size == 1) {
            deselect(_selectedIndices.first())
        } else if (maxSelectableIndices > -1 && maxSelectableIndices <= _selectedIndices.size) {
            return
        }

        if (!_selectedIndices.add(index)) return

        notifyItemChanged(index)
        for (listener in listeners) {
            listener.onIndexSelected(index)
        }
    }

    fun deselect(index: Int) {
        checkIndexRange(index)
        if (!_selectedIndices.remove(index)) return

        notifyItemChanged(index)
        for (listener in listeners) {
            listener.onIndexDeselected(index)
        }
    }

    fun toggle(index: Int) {
        checkIndexRange(index)
        if (!isSelected(index)) {
            select(index)
        } else {
            deselect(index)
        }
    }

    private fun checkIndexRange(index: Int) {
        if (index < 0 || index >= items.size)
            throw ArrayIndexOutOfBoundsException(index)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
        holder.isSelected = isSelected(position)
    }

    override fun getItemCount() = items.size
}
