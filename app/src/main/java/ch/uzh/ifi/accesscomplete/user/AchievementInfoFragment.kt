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

import android.animation.LayoutTransition
import android.animation.LayoutTransition.APPEARING
import android.animation.LayoutTransition.DISAPPEARING
import android.animation.TimeAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.user.achievements.Achievement
import ch.uzh.ifi.accesscomplete.ktx.tryStartActivity
import ch.uzh.ifi.accesscomplete.util.Transforms
import ch.uzh.ifi.accesscomplete.util.animateFrom
import ch.uzh.ifi.accesscomplete.util.animateTo
import ch.uzh.ifi.accesscomplete.util.applyTransforms
import kotlinx.android.synthetic.main.fragment_achievement_info.*


/** Shows details for a certain level of one achievement as a fake-dialog.
 *  There are two modes:
 *
 *  1. Show details of a newly achieved achievement. The achievement icon animates in in a fancy way
 *     and some shining animation is played. The unlocked links are shown.
 *
 *  2. Show details of an already achieved achievement. The achievement icon animates from another
 *     view to its current position, no shining animation is played. Also, the unlocked links are
 *     not shown because they can be looked at in the links screen.
 *
 *  It is not a real dialog because a real dialog has its own window, or in other words, has a
 *  different root view than the rest of the UI. However, for the calculation to animate the icon
 *  from another view to the position in the "dialog", there must be a common root view.
 *  */
class AchievementInfoFragment : Fragment(R.layout.fragment_achievement_info) {

    /** View from which the achievement icon is animated from (and back on dismissal)*/
    private var achievementIconBubble: View? = null

    var isShowing: Boolean = false
        private set

    // need to keep the animators here to be able to clear them on cancel
    private val currentAnimators: MutableList<ViewPropertyAnimator> = mutableListOf()
    private var shineAnimation: TimeAnimator? = null

    private val layoutTransition: LayoutTransition = LayoutTransition()

    /* ---------------------------------------- Lifecycle --------------------------------------- */

    init {
        layoutTransition.disableTransitionType(APPEARING)
        layoutTransition.disableTransitionType(DISAPPEARING)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogAndBackgroundContainer.setOnClickListener { dismiss() }
        // in order to not show the scroll indicators
        unlockedLinksList.isNestedScrollingEnabled = false
        unlockedLinksList.layoutManager = object : LinearLayoutManager(requireContext(), VERTICAL, false) {
            override fun canScrollVertically() = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        achievementIconBubble = null
        clearAnimators()
        shineAnimation?.cancel()
        shineAnimation = null
    }


    /* ---------------------------------------- Interface --------------------------------------- */

    /** Show as details of a tapped view */
    fun show(achievement: Achievement, level: Int, achievementBubbleView: View): Boolean {
        if (currentAnimators.isNotEmpty()) return false
        isShowing = true
        this.achievementIconBubble = achievementBubbleView

        bind(achievement, level, false)
        animateInFromView(achievementBubbleView)
        return true
    }

    /** Show as new achievement achieved/unlocked */
    fun showNew(achievement: Achievement, level: Int): Boolean {
        if (currentAnimators.isNotEmpty()) return false
        isShowing = true

        bind(achievement, level, true)
        animateIn()
        return true
    }

    fun dismiss(): Boolean {
        if (currentAnimators.isNotEmpty()) return false
        isShowing = false
        animateOut(achievementIconBubble)
        return true
    }

    /* ----------------------------------- Animating in and out --------------------------------- */

    private fun bind(achievement: Achievement, level: Int, showLinks: Boolean) {
        achievementIconView.icon = ContextCompat.getDrawable(requireContext(), achievement.icon)
        achievementIconView.level = level
        achievementTitleText.setText(achievement.title)

        achievementDescriptionText.isGone = achievement.description == null
        if (achievement.description != null) {
            val arg = achievement.getPointThreshold(level)
            achievementDescriptionText.text = resources.getString(achievement.description, arg)
        } else {
            achievementDescriptionText.text = ""
        }

        val unlockedLinks = achievement.unlockedLinks[level].orEmpty()
        val hasNoUnlockedLinks = unlockedLinks.isEmpty() || !showLinks
        unlockedLinkTitleText.isGone = hasNoUnlockedLinks
        unlockedLinksList.isGone = hasNoUnlockedLinks
        if (hasNoUnlockedLinks) {
            unlockedLinksList.adapter = null
        } else {
            unlockedLinkTitleText.setText(
                if (unlockedLinks.size == 1) R.string.achievements_unlocked_link
                else R.string.achievements_unlocked_links
            )
            unlockedLinksList.adapter = LinksAdapter(unlockedLinks, this::openUrl)
        }
    }

    private fun animateIn() {
        dialogAndBackgroundContainer.visibility = View.VISIBLE

        shineAnimation?.cancel()
        val anim = TimeAnimator()
        anim.setTimeListener { _, _, deltaTime ->
            shineView1.rotation += deltaTime / 50f
            shineView2.rotation -= deltaTime / 100f
        }
        anim.start()
        shineAnimation = anim

        clearAnimators()

        currentAnimators.addAll(
            createShineFadeInAnimations() +
            createDialogPopInAnimations(DIALOG_APPEAR_DELAY_IN_MS) +
            listOf(
                createFadeInBackgroundAnimation(),
                createAchievementIconPopInAnimation()
            )
        )
        currentAnimators.forEach { it.start() }
    }

    private fun animateInFromView(questBubbleView: View) {
        questBubbleView.visibility = View.INVISIBLE
        dialogAndBackgroundContainer.visibility = View.VISIBLE

        shineView1.visibility = View.GONE
        shineView2.visibility = View.GONE

        currentAnimators.addAll(createDialogPopInAnimations() + listOf(
            createFadeInBackgroundAnimation(),
            createAchievementIconFlingInAnimation(questBubbleView)
        ))
        currentAnimators.forEach { it.start() }
    }

    private fun animateOut(questBubbleView: View?) {

        dialogContainer.layoutTransition = null

        val iconAnimator = if (questBubbleView != null) {
            createAchievementIconFlingOutAnimation(questBubbleView)
        } else {
            createAchievementIconPopOutAnimation()
        }

        currentAnimators.addAll(
            createDialogPopOutAnimations() +
            createShineFadeOutAnimations() +
            listOf(
                createFadeOutBackgroundAnimation(),
                iconAnimator
            )
        )
        currentAnimators.forEach { it.start() }
    }

    private fun createAchievementIconFlingInAnimation(sourceView: View): ViewPropertyAnimator {
        sourceView.visibility = View.INVISIBLE
        val root = sourceView.rootView as ViewGroup
        achievementIconView.applyTransforms(Transforms.IDENTITY)
        achievementIconView.alpha = 1f
        return achievementIconView.animateFrom(sourceView, root)
            .setDuration(ANIMATION_TIME_IN_MS)
            .setInterpolator(OvershootInterpolator())
    }

    private fun createAchievementIconFlingOutAnimation(targetView: View): ViewPropertyAnimator {
        val root = targetView.rootView as ViewGroup
        return achievementIconView.animateTo(targetView, root)
            .setDuration(ANIMATION_TIME_OUT_MS)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                targetView.visibility = View.VISIBLE
                achievementIconBubble = null
            }
    }

    private fun createShineFadeInAnimations(): List<ViewPropertyAnimator> {
        return listOf(shineView1, shineView2).map {
            it.visibility = View.VISIBLE
            it.alpha = 0f
            it.animate()
                .alpha(1f)
                .setDuration(ANIMATION_TIME_IN_MS)
                .setInterpolator(DecelerateInterpolator())
        }
    }

    private fun createShineFadeOutAnimations(): List<ViewPropertyAnimator> {
        return listOf(shineView1, shineView2).map {
            it.animate()
                .alpha(0f)
                .setDuration(ANIMATION_TIME_OUT_MS)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    shineAnimation?.cancel()
                    shineAnimation = null
                    it.visibility = View.GONE
                }
        }
    }

    private fun createAchievementIconPopInAnimation(): ViewPropertyAnimator {
        achievementIconView.alpha = 0f
        achievementIconView.scaleX = 0f
        achievementIconView.scaleY = 0f
        achievementIconView.rotationY = -180f
        return achievementIconView.animate()
            .alpha(1f)
            .scaleX(1f).scaleY(1f)
            .rotationY(360f)
            .setDuration(ANIMATION_TIME_NEW_ACHIEVEMENT_IN_MS)
            .setInterpolator(DecelerateInterpolator())
    }

    private fun createAchievementIconPopOutAnimation(): ViewPropertyAnimator {
        return achievementIconView.animate()
            .alpha(0f)
            .scaleX(0.5f).scaleY(0.5f)
            .setDuration(ANIMATION_TIME_OUT_MS)
            .setInterpolator(AccelerateInterpolator())
    }

    private fun createDialogPopInAnimations(startDelay: Long = 0): List<ViewPropertyAnimator> {
        return listOf(dialogContentContainer, dialogBubbleBackground).map {
            it.alpha = 0f
            it.scaleX = 0.5f
            it.scaleY = 0.5f
            it.translationY = 0f
            /* For the "show new achievement" mode, only the icon is shown first and only after a
            *  delay, the dialog with the description etc.
            *  This icon is in the center at first and should animate up while the dialog becomes
            *  visible. This movement is solved via a (default) layout transition here for which the
            *  APPEARING transition type is disabled because we animate the alpha ourselves. */
            it.isGone = startDelay > 0
            it.animate()
                .setStartDelay(startDelay)
                .withStartAction {
                    if (startDelay > 0) {
                        dialogContainer.layoutTransition = layoutTransition
                        it.visibility = View.VISIBLE
                    }
                }
                .alpha(1f)
                .scaleX(1f).scaleY(1f)
                .setDuration(ANIMATION_TIME_IN_MS)
                .setInterpolator(OvershootInterpolator())
        }
    }

    private fun createDialogPopOutAnimations(): List<ViewPropertyAnimator> {
        return listOf(dialogContentContainer, dialogBubbleBackground).map {
            it.animate()
                .alpha(0f)
                .setStartDelay(0)
                .scaleX(0.5f).scaleY(0.5f)
                .translationYBy(it.height * 0.2f)
                .setDuration(ANIMATION_TIME_OUT_MS)
                .setInterpolator(AccelerateInterpolator())
        }
    }

    private fun createFadeInBackgroundAnimation(): ViewPropertyAnimator {
        dialogBackground.alpha = 0f
        return dialogBackground.animate()
            .alpha(1f)
            .setDuration(ANIMATION_TIME_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { currentAnimators.clear() }
    }

    private fun createFadeOutBackgroundAnimation(): ViewPropertyAnimator {
        return dialogBackground.animate()
            .alpha(0f)
            .setDuration(ANIMATION_TIME_OUT_MS)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                dialogAndBackgroundContainer.visibility = View.INVISIBLE
                currentAnimators.clear()
            }
    }

    private fun clearAnimators() {
        for (anim in currentAnimators) {
            anim.cancel()
        }
        currentAnimators.clear()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        tryStartActivity(intent)
    }

    companion object {
        const val ANIMATION_TIME_NEW_ACHIEVEMENT_IN_MS = 1000L
        const val ANIMATION_TIME_IN_MS = 400L
        const val DIALOG_APPEAR_DELAY_IN_MS = 1600L
        const val ANIMATION_TIME_OUT_MS = 300L
    }
}
