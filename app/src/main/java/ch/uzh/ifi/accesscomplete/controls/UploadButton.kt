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

package ch.uzh.ifi.accesscomplete.controls

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.core.view.isInvisible
import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.view_upload_button.view.*

class UploadButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr)  {

    var uploadableCount: Int = 0
    set(value) {
        field = value
        textView.text = value.toString()
        textView.isInvisible = value == 0
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        iconView.alpha = if (enabled) 1f else 0.5f
    }

    var showProgress: Boolean = false
    set(value) {
        field = value
        progressView.isInvisible = !value
    }

    init {
        inflate(context, R.layout.view_upload_button, this)
        clipToPadding = false
    }
}
