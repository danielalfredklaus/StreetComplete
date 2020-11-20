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

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import ch.uzh.ifi.accesscomplete.R

/** Select one items from a groupable list of items  */
class GroupedImageSelectAdapter<T>(val gridLayoutManager: GridLayoutManager) :
    RecyclerView.Adapter<ItemViewHolder>() {

    var cellLayoutId = R.layout.cell_labeled_image_select
    var groupCellLayoutId = R.layout.cell_panorama_select

    private var _items = mutableListOf<GroupableDisplayItem<T>>()
    var items: List<GroupableDisplayItem<T>>
    set(value) {
        _items.clear()
        _items.addAll(value)
        selectedItem = null
        for (listener in listeners) {
            listener(null)
        }
        notifyDataSetChanged()
    }
    get() = _items.toList()

    var selectedItem: GroupableDisplayItem<T>? = null
        private set

    private val selectedIndex get() = selectedItem?.let { _items.indexOf(it) } ?: -1

    val listeners = mutableListOf<(GroupableDisplayItem<T>?) -> Unit>()

    init {
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (_items[position].isGroup) gridLayoutManager.spanCount else 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutId = if (viewType == GROUP) groupCellLayoutId else cellLayoutId
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val holder = ItemViewHolder(view)
        holder.onClickListener = ::toggle
        return holder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(_items[position])
        holder.isSelected = selectedItem?.let { _items.indexOf(it) == position } == true
        holder.isGroupExpanded = getGroup(selectedIndex) == position
    }

    private fun toggle(index: Int) {
        val prevSelectedItem = selectedItem
        selectedItem = if (selectedItem == null || prevSelectedItem !== _items[index]) {
            _items[index]
        } else {
            null
        }

        val selectedItem = selectedItem
        if (prevSelectedItem != null) {
            val prevSelectedIndex = _items.indexOf(prevSelectedItem)
            notifyItemChanged(prevSelectedIndex)

            val previousGroupIndex = getGroup(prevSelectedIndex)
            if (previousGroupIndex != -1) {
                if (selectedItem == null || previousGroupIndex != getGroup(_items.indexOf(selectedItem))) {
                    retractGroup(previousGroupIndex)
                }
            }
        }
        if (selectedItem != null) {
            val selectedIndex = _items.indexOf(selectedItem)
            notifyItemChanged(selectedIndex)

            if (selectedItem.isGroup) {
                if (prevSelectedItem == null || getGroup(_items.indexOf(prevSelectedItem)) != selectedIndex) {
                    expandGroup(selectedIndex)
                }
            }
        }
        for (listener in listeners) {
            listener(selectedItem)
        }
    }

    private fun getGroup(index: Int): Int {
        for (i in index downTo 0) {
            if (_items[i].isGroup) return i
        }
        return -1
    }

    private fun expandGroup(index: Int) {
        val item = _items[index]
        val subItems = item.items!!
        for (i in subItems.indices) {
            _items.add(index + i + 1, subItems[i])
        }
        notifyItemChanged(index)
        notifyItemRangeInserted(index + 1, subItems.size)
    }

    private fun retractGroup(index: Int) {
        val item = _items[index]
        val subItems = item.items!!
        for (i in subItems.indices) {
            _items.removeAt(index + 1)
        }
        notifyItemChanged(index)
        notifyItemRangeRemoved(index + 1, subItems.size)
    }

    override fun getItemCount() = _items.size

    override fun getItemViewType(position: Int) = if (_items[position].isGroup) GROUP else CELL

    companion object {
        private const val GROUP = 0
        private const val CELL = 1
    }
}
