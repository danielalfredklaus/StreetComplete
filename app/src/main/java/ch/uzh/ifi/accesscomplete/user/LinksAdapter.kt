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

package ch.uzh.ifi.accesscomplete.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.user.achievements.Link
import ch.uzh.ifi.accesscomplete.view.ListAdapter
import kotlinx.android.synthetic.main.row_link_item.view.*

/** Adapter for a list of links */
class LinksAdapter(links: List<Link>, private val onClickLink: (url: String) -> Unit)
    : ListAdapter<Link>(links) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_link_item, parent, false))

    inner class ViewHolder(itemView: View) : ListAdapter.ViewHolder<Link>(itemView) {
        override fun onBind(with: Link) {
            if (with.icon != null) {
                itemView.linkIconImageView.setImageResource(with.icon)
            } else {
                itemView.linkIconImageView.setImageDrawable(null)
            }
            itemView.linkTitleTextView.text = with.title
            if (with.description != null) {
                itemView.linkDescriptionTextView.setText(with.description)
            } else {
                itemView.linkDescriptionTextView.text = ""
            }
            itemView.setOnClickListener { onClickLink(with.url) }
        }
    }
}
