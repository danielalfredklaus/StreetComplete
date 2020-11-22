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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.view.isInvisible
import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.view_answers_counter.view.*

class AnswersCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var uploadedCount: Int = 0
        set(value) {
            field = value
            textView.text = value.toString()
        }

    var showProgress: Boolean = false
        set(value) {
            field = value
            progressView.isInvisible = !value
        }

    init {
        inflate(context, R.layout.view_answers_counter, this)
    }

    fun setUploadedCount(uploadedCount: Int, animate: Boolean) {
        if (this.uploadedCount < uploadedCount && animate) {
            animateChange()
        }
        this.uploadedCount = uploadedCount
        contentDescription = context.getString(R.string.total_quests_solved, uploadedCount)
    }

    private fun animateChange() {
        textView.animate()
            .scaleX(1.6f).scaleY(1.6f)
            .setInterpolator(DecelerateInterpolator(2f))
            .setDuration(100)
            .withEndAction {
                textView.animate()
                    .scaleX(1f).scaleY(1f)
                    .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
            }
    }
}
