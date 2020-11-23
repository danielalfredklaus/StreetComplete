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

package ch.uzh.ifi.accesscomplete.tutorial

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.toDp
import kotlinx.android.synthetic.main.fragment_tutorial.*

/** Shows a short tutorial for first-time users */
class TutorialFragment : Fragment(R.layout.fragment_tutorial) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentPageIndex: Int = 0

    interface Listener {
        fun onFinishedTutorial()
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFittingToSystemWindowInsets()
        updateIndicatorDots()

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity?.window?.statusBarColor = resources.getColor(R.color.primary, requireContext().theme)

        nextButton.setOnClickListener {
            when(currentPageIndex) {
                0 -> {
                    currentPageIndex = 1
                    fromStep1ToStep2()
                }
                1 -> {
                    currentPageIndex = 2
                    fromStep2ToStep3()
                }
                2 -> {
                    currentPageIndex = 3
                    fromStep3ToStep4()
                }
                MAX_PAGE_INDEX -> {
                    nextButton.isEnabled = false
                    listener?.onFinishedTutorial()
                }
            }
        }

        backButton.setOnClickListener {
            when(currentPageIndex) {
                1 -> {
                    currentPageIndex = 0
                    fromStep2ToStep1()
                }
                2 -> {
                    currentPageIndex = 1
                    fromStep3ToStep2()
                }
                3 -> {
                    currentPageIndex = 2
                    fromStep4ToStep3()
                }
            }
        }

        skipButton.setOnClickListener {
            skipButton.isEnabled = false
            nextButton.isEnabled = false
            listener?.onFinishedTutorial()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun setupFittingToSystemWindowInsets() {
        view?.setOnApplyWindowInsetsListener { v: View?, insets: WindowInsets ->
            view?.setPadding(
                insets.systemWindowInsetLeft,
                insets.systemWindowInsetTop,
                insets.systemWindowInsetRight,
                insets.systemWindowInsetBottom
            )
            insets
        }
    }

    private fun fromStep1ToStep2() {
        updateIndicatorDots()

        skipButton.visibility = View.GONE
        backButton.visibility = View.VISIBLE

        fadeOutStepDescription(tutorialStepIntro)
        fadeInStepDescription(tutorialStepQuests, tutorialStepQuestsTextView)

        fadeOutLogo()
        dropPin(questPin1)
    }

    private fun fromStep2ToStep3() {
        updateIndicatorDots()

        fadeOutStepDescription(tutorialStepQuests)
        fadeInStepDescription(tutorialStepSolvingQuests, tutorialStepSolvingQuestsTextView)

        dropPin(questPin2)
    }

    private fun fromStep3ToStep4() {
        updateIndicatorDots()
        nextButton.setText(R.string.letsgo)

        fadeOutStepDescription(tutorialStepSolvingQuests)
        fadeInStepDescription(tutorialStepStaySafe, tutorialStepStaySafeTextView)

        dropPin(questPin3)
    }

    private fun fromStep4ToStep3() {
        updateIndicatorDots()
        nextButton.setText(R.string.next)

        fadeOutStepDescription(tutorialStepStaySafe)
        fadeInStepDescription(tutorialStepSolvingQuests, tutorialStepSolvingQuestsTextView)

        fadeOutPin(questPin3)
    }

    private fun fromStep3ToStep2() {
        updateIndicatorDots()

        fadeOutStepDescription(tutorialStepSolvingQuests)
        fadeInStepDescription(tutorialStepQuests, tutorialStepQuestsTextView)

        fadeOutPin(questPin2)
    }

    private fun fromStep2ToStep1() {
        updateIndicatorDots()

        backButton.visibility = View.GONE
        skipButton.visibility = View.VISIBLE

        fadeOutStepDescription(tutorialStepQuests)
        fadeInStepDescription(tutorialStepIntro, titleTextView)

        fadeOutPin(questPin1)
        fadeInLogo()
    }

    private fun fadeOutLogo() {
        logoImageView.animate()
            .setDuration(400)
            .setInterpolator(AccelerateInterpolator())
            .alpha(0f)
            .start()
    }

    private fun fadeInLogo() {
        logoImageView.animate()
            .setDuration(400)
            .setInterpolator(AccelerateInterpolator())
            .alpha(1f)
            .start()
    }

    private fun dropPin(pin: View) {
        pin.translationY = (-200f).toDp(requireContext())
        pin.animate()
            .setStartDelay(800)
            .setInterpolator(BounceInterpolator())
            .setDuration(400)
            .translationY(0f)
            .alpha(1f)
            .start()
    }

    private fun fadeOutPin(pin: View) {
        pin.animate()
            .setStartDelay(0)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(300)
            .alpha(0f)
            .start()
    }

    private fun fadeOutStepDescription(scrollView: ScrollView) {
        scrollView.animate()
            .setStartDelay(0)
            .setDuration(300)
            .alpha(0f)
            .translationY(100f.toDp(requireContext()))
            .withEndAction { scrollView.visibility = View.GONE }
            .start()
    }

    private fun fadeInStepDescription(scrollView: ScrollView, newFocusView : View) {
        scrollView.translationY = (-100f).toDp(requireContext())
        scrollView.animate()
            .withStartAction { scrollView.visibility = View.VISIBLE }
            .setStartDelay(400)
            .setDuration(300)
            .alpha(1f)
            .translationY(0f)
            .withEndAction {
                newFocusView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
            .start()
    }

    private fun updateIndicatorDots() {
        listOf(dot1, dot2, dot3, dot4).forEachIndexed { index, dot ->
            dot.setImageResource(
                if (currentPageIndex == index) R.drawable.indicator_dot_selected
                else R.drawable.indicator_dot_default
            )
        }
    }

    companion object {
        private const val MAX_PAGE_INDEX = 3
    }
}
