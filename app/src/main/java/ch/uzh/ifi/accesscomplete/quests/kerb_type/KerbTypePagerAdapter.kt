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

package ch.uzh.ifi.accesscomplete.quests.kerb_type

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.view.image_select.Item

class KerbTypePagerAdapter(private val context: Context, private val valueItems: List<Item<String>>) : PagerAdapter() {

    override fun getPageWidth(position: Int): Float {
        return 1f
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = valueItems[position]
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.kerb_type_pager_item, container, false)

        val title = layout.findViewById<TextView>(R.id.itemTitle)
        val description = layout.findViewById<TextView>(R.id.itemDescription)
        val imageView = layout.findViewById<ImageView>(R.id.itemImageView)
        title.text = context.resources.getText(item.titleId!!)
        description.text = context.resources.getText(item.descriptionId!!)
        imageView.setImageResource(item.drawableId!!)

        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getCount(): Int {
        return valueItems.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }
}
