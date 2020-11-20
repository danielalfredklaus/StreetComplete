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

package ch.uzh.ifi.accesscomplete.about

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.getYamlObject
import ch.uzh.ifi.accesscomplete.view.ListAdapter
import kotlinx.android.synthetic.main.row_changelog.view.*

/** Shows the full changelog */
class ChangelogFragment : Fragment(R.layout.fragment_changelog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val changelog = readChangelog(resources)
        val changelogList = view.findViewById<RecyclerView>(R.id.changelogList)
        changelogList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        changelogList.adapter = ChangelogAdapter(changelog)
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.about_title_changelog)
    }
}

/** A dialog that shows the changelog */
class WhatsNewDialog(context: Context, sinceVersion: String)
    : AlertDialog(context, R.style.Theme_Bubble_Dialog) {

    init {
        val fullChangelog = readChangelog(context.resources)
        var currentVersionIndex = fullChangelog.indexOfFirst { it.title == sinceVersion }
        // if version not found, just show the last one
        if (currentVersionIndex == -1) currentVersionIndex = 1
        val changelog = fullChangelog.subList(0, currentVersionIndex)

        val view = LayoutInflater.from(context).inflate(R.layout.fragment_changelog, null, false)
        val changelogList = view.findViewById<RecyclerView>(R.id.changelogList)
        changelogList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        changelogList.adapter = ChangelogAdapter(changelog)

        setTitle(R.string.title_whats_new)
        setView(view)
        setButton(DialogInterface.BUTTON_POSITIVE, context.resources.getText(android.R.string.ok), null, null)
    }
}

class ChangelogAdapter(changelog: List<Release>) : ListAdapter<Release>(changelog) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_changelog, parent, false))

    inner class ViewHolder(itemView: View) : ListAdapter.ViewHolder<Release>(itemView) {
        override fun onBind(with: Release) {
            itemView.titleLabel.text = with.title
            itemView.descriptionLabel.setHtml(addedLinks(with.description))
        }
    }
}

data class Release(val title: String, val description: String)

private fun readChangelog(resources: Resources) =
    resources.getYamlObject<LinkedHashMap<String, String>>(R.raw.changelog)
        .map { Release(it.key, it.value) }

private fun addedLinks(description: String): String {
    return description
        .replace(Regex("(?<=[\\s(]|^)#(\\d+)")) { matchResult ->
            val issue = matchResult.groupValues[1]
            "<a href=\"https://github.com/westnordost/StreetComplete/issues/$issue\">#$issue</a>"
        }
        .replace(Regex("(?<=[\\s(]|^)@([a-zA-Z\\d-]+)")) { matchResult ->
            val contributor = matchResult.groupValues[1]
            "<a href=\"https://github.com/$contributor\">@$contributor</a>"
        }
}
