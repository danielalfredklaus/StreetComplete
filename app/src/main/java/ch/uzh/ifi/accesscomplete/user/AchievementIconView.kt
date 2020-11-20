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

import android.content.Context
import android.graphics.Outline
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.view_achievement_icon.view.*

/** Shows an achievement icon with its frame and level indicator */
class AchievementIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr)  {

    var icon: Drawable?
        set(value) { iconView.setImageDrawable(value) }
        get() = iconView.drawable

    var level: Int
        set(value) {
            levelText.text = value.toString()
            levelText.isInvisible = value < 2
        }
        get() = levelText.text.toString().toIntOrNull() ?: 0

    init {
        inflate(context, R.layout.view_achievement_icon, this)
        outlineProvider = AchievementFrameOutlineProvider
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object AchievementFrameOutlineProvider : ViewOutlineProvider() {
    private val points = arrayOf(
        0.45,0.98,
        0.47,0.99,
        0.50,1.00,
        0.53,0.99,
        0.55,0.98,

        0.98,0.55,
        0.99,0.53,
        1.00,0.50,
        0.99,0.47,
        0.98,0.45,

        0.55,0.02,
        0.53,0.01,
        0.50,0.00,
        0.47,0.01,
        0.45,0.02,

        0.02,0.45,
        0.01,0.47,
        0.00,0.50,
        0.01,0.53,
        0.02,0.55,

        0.45,0.98
    )

    override fun getOutline(view: View, outline: Outline) {
        val w = view.width
        val h = view.height
        if (w == 0 || h == 0) return

        val p = Path()
        p.moveTo((points[0] * w).toFloat(), (points[1] * h).toFloat())
        for (i in 2 until points.size step 2) {
            p.lineTo((points[i] * w).toFloat(), (points[i+1] * h).toFloat())
        }
        outline.setConvexPath(p)
    }
}
