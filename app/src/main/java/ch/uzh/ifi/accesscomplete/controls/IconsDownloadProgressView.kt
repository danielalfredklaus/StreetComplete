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
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RelativeLayout
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.toPx
import ch.uzh.ifi.accesscomplete.view.CircularMaskFrameLayout
import kotlinx.android.synthetic.main.view_icons_download_progress.view.*
import java.util.*

/** view that shows a queue of IconProgressViews moving in from the right and moving out to the
 *  left when they are done */
class IconsDownloadProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CircularMaskFrameLayout(context, attrs, defStyleAttr)  {

    private var currentView: IconProgressView? = null
    private val iconQueue: Queue<Drawable> = LinkedList()

    init {
        inflate(context, R.layout.view_icons_download_progress, this)
    }

    /** set the given icon and resets the queue */
    fun setIcon(icon: Drawable) = synchronized(this) {
        iconQueue.clear()
        currentView?.let { iconProgressViewContainer.removeView(it) }
        val newView = createProgressView(icon)
        iconProgressViewContainer.addView(newView)
        newView.showProgressAnimation()
        currentView = newView
    }

    /** sets the next icon to show the progress for. It will be animate in after the next call
     *  to pollIcon or immediately if there is no current icon  */
    fun enqueueIcon(icon: Drawable) = synchronized(this) {
        iconQueue.add(icon)
        if (currentView == null) animateToNextIcon()
    }

    /** executes a finished-animation on the current icon, animates it out and animates in the next
     *  icon, if there is one. */
    fun pollIcon() {
        currentView?.showFinishedAnimation(onFinished = {
            animateToNextIcon()
        })
    }

    private fun animateToNextIcon() = synchronized(this) {
        // move out old icon...
        currentView?.let {
            animateOutIcon(it)
            currentView = null
        }
        // add new icon, if there is one
        val icon = iconQueue.poll()
        if (icon != null) {
            val newView = createProgressView(icon)
            iconProgressViewContainer.addView(newView)
            animateInIcon(newView)
            currentView = newView
        }
    }

    private fun createProgressView(icon: Drawable): IconProgressView {
        val v = IconProgressView(context)
        v.icon = icon
        val size = ICON_SIZE.toPx(context).toInt()
        val layoutParams = RelativeLayout.LayoutParams(size, size)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        v.layoutParams = layoutParams
        return v
    }

    private fun animateInIcon(view: IconProgressView) {
        view.translationX = ICON_INITIAL_X_OFFSET.toPx(context)
        view.alpha = ICON_INITIAL_ALPHA
        view.scaleX = ICON_INITIAL_SCALE
        view.scaleY = ICON_INITIAL_SCALE
        view.showProgressAnimation()
        view.animate()
            .translationX(0f)
            .alpha(1f)
            .scaleX(1f).scaleY(1f)
            .setDuration(ICON_IN_OUT_DURATION)
            .start()
    }

    private fun animateOutIcon(view: IconProgressView) {
        view.animate()
            .translationX(-(ICON_INITIAL_X_OFFSET).toPx(context))
            .alpha(ICON_INITIAL_ALPHA)
            .scaleX(ICON_INITIAL_SCALE)
            .scaleY(ICON_INITIAL_SCALE)
            .setDuration(ICON_IN_OUT_DURATION)
            .withEndAction { iconProgressViewContainer.removeView(view) }
            .start()
    }

    companion object {
        const val ICON_INITIAL_SCALE = 0.4f
        const val ICON_INITIAL_ALPHA = 0.6f
        const val ICON_INITIAL_X_OFFSET = 64f
        const val ICON_IN_OUT_DURATION = 500L
        const val ICON_SIZE = 64f
    }
}
