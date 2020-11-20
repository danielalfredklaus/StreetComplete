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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface DisplayItem<T> {
    val value: T?
    val image: Image?
    val title: Text?
    val description: Text?
}

interface GroupableDisplayItem<T> : DisplayItem<T> {
    val items: List<GroupableDisplayItem<T>>?
    val isGroup: Boolean get() = !items.isNullOrEmpty()
}


sealed class Text
data class ResText(@StringRes val resId: Int) : Text()
data class CharSequenceText(val text: CharSequence) : Text()

fun TextView.setText(text: Text?) {
    when(text) {
        is ResText -> setText(text.resId)
        is CharSequenceText -> setText(text.text)
        null -> setText("")
    }
}

sealed class Image
data class ResImage(@DrawableRes val resId: Int) : Image()
data class DrawableImage(val drawable: Drawable) : Image()
data class BitmapImage(val bitmap: Bitmap) : Image()
data class URIImage(val uri: Uri) : Image()

fun ImageView.setImage(image: Image?) {
    when(image) {
        is ResImage -> setImageResource(image.resId)
        is DrawableImage -> setImageDrawable(image.drawable)
        is BitmapImage -> setImageBitmap(image.bitmap)
        is URIImage -> setImageURI(image.uri)
        null -> setImageDrawable(null)
    }
}
