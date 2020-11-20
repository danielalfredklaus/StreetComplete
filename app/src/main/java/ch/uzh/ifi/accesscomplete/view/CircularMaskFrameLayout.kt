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

package ch.uzh.ifi.accesscomplete.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import ch.uzh.ifi.accesscomplete.R
import kotlin.math.sqrt

open class CircularMaskFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    var circularity: Float = 1f
    set(value) {
        val newVal = value.coerceIn(0f,1f)
        field = newVal
        invalidate()
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.CircularMaskFrameLayout) {
            circularity = getFloat(R.styleable.CircularMaskFrameLayout_circularity, 1f)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val diff = 2 * sqrt(w*w + h*h) / (w+h) - 0.9f
        val xoffs = diff * width * (1 - circularity)
        val yoffs = diff * height * (1 - circularity)

        val path = Path()
        path.addOval(
            RectF(0f - xoffs/2, 0f - yoffs/2, width + xoffs, height + yoffs),
            Path.Direction.CW
        )
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
    }
}
