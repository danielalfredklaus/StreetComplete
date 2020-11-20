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

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import android.widget.RelativeLayout
import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.view_icon_progress.view.*

/** Shows an icon, surrounded by a circular progress bar and a finished-checkmark */
class IconProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr)  {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var wobbleAnimator: Animator? = null

    var icon: Drawable?
        set(value) { iconView.setImageDrawable(value) }
        get() = iconView.drawable


    private val animatorDurationScale: Float get() =
       Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mainHandler.removeCallbacksAndMessages(null)
    }

    fun showProgressAnimation() {
        wobbleAnimator = AnimatorInflater.loadAnimator(context, R.animator.progress_wobble).apply {
            setTarget(iconView)
            start()
        }
        progressView.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    fun showFinishedAnimation(onFinished: () -> Unit) {
        wobbleAnimator?.cancel()
        progressView.animate()
            .alpha(0f)
            .setDuration(300)
            .start()
        checkmarkView.animate()
            .setDuration(300)
            .alpha(1f)
            .start()

        (checkmarkView.drawable as? AnimatedVectorDrawable)?.start()

        val hardcodedCheckmarkAnimationDuration = (animatorDurationScale * 650).toLong()
        mainHandler.postDelayed(onFinished, hardcodedCheckmarkAnimationDuration)
    }

    init {
        inflate(context, R.layout.view_icon_progress, this)
    }
}
