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

package ch.uzh.ifi.accesscomplete.quests.note_discussion

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnLayout

import java.io.File

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.util.decodeScaledBitmapAndNormalize
import ch.uzh.ifi.accesscomplete.view.ListAdapter

class NoteImageAdapter(list: List<String>, private val context: Context) : ListAdapter<String>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<String> {
        val inflater = LayoutInflater.from(parent.context)
        return NoteImageViewHolder(inflater.inflate(R.layout.cell_image_thumbnail, parent, false))
    }

    private inner class NoteImageViewHolder(itemView: View) : ViewHolder<String>(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            imageView.setOnClickListener {
                val index = adapterPosition
                if (index > -1) onClickDelete(index)
            }
        }

        override fun onBind(with: String) {
            itemView.doOnLayout {
                val bitmap = decodeScaledBitmapAndNormalize(with, imageView.width, imageView.height)
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun onClickDelete(index: Int) {
        AlertDialog.Builder(context)
            .setMessage(R.string.quest_leave_new_note_photo_delete_title)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ -> delete(index) }
            .show()
    }

    private fun delete(index: Int) {
        val imagePath = list.removeAt(index)
        val image = File(imagePath)
        if (image.exists()) {
            image.delete()
        }
        notifyItemRemoved(index)
    }
}
