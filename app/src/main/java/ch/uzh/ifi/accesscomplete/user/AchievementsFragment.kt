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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.user.UserStore
import ch.uzh.ifi.accesscomplete.data.user.achievements.Achievement
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserAchievementsSource
import ch.uzh.ifi.accesscomplete.ktx.awaitPreDraw
import ch.uzh.ifi.accesscomplete.ktx.toPx
import ch.uzh.ifi.accesscomplete.view.GridLayoutSpacingItemDecoration
import ch.uzh.ifi.accesscomplete.view.ListAdapter
import kotlinx.android.synthetic.main.cell_achievement.view.*
import kotlinx.android.synthetic.main.fragment_achievements.*
import kotlinx.coroutines.*
import javax.inject.Inject

/** Shows the icons for all achieved achievements and opens a AchievementInfoFragment to show the
 *  details on click. */
class AchievementsFragment : Fragment(R.layout.fragment_achievements),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    @Inject internal lateinit var userAchievementsSource: UserAchievementsSource
    @Inject internal lateinit var userStore: UserStore

    private var actualCellWidth: Int = 0

    init {
        Injector.applicationComponent.inject(this)
    }

    interface Listener {
        fun onClickedAchievement(achievement: Achievement, level: Int, achievementBubbleView: View)
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    /* --------------------------------------- Lifecycle ---------------------------------------- */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()
        val minCellWidth = 144f.toPx(ctx)
        val itemSpacing = ctx.resources.getDimensionPixelSize(R.dimen.achievements_item_margin)

        launch {
            view.awaitPreDraw()

            emptyText.visibility = View.GONE

            val spanCount = (view.width / (minCellWidth + itemSpacing)).toInt()
            actualCellWidth = (view.width.toFloat() / spanCount - itemSpacing).toInt()

            val layoutManager = GridLayoutManager(ctx, spanCount, RecyclerView.VERTICAL, false)
            achievementsList.layoutManager = layoutManager
            achievementsList.addItemDecoration(GridLayoutSpacingItemDecoration(itemSpacing))
            achievementsList.clipToPadding = false

            val achievements = withContext(Dispatchers.IO) {
                userAchievementsSource.getAchievements()
            }
            achievementsList.adapter = AchievementsAdapter(achievements)

            emptyText.isGone = achievements.isNotEmpty()
        }
    }

    override fun onStart() {
        super.onStart()

        if (userStore.isSynchronizingStatistics) {
            emptyText.setText(R.string.stats_are_syncing)
        } else {
            emptyText.setText(R.string.achievements_empty)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    /* -------------------------------------- Interaction --------------------------------------- */

    private inner class AchievementsAdapter(achievements: List<Pair<Achievement, Int>>
    ) : ListAdapter<Pair<Achievement, Int>>(achievements) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_achievement, parent, false)
            view.updateLayoutParams {
                width = actualCellWidth
                height = actualCellWidth
            }
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : ListAdapter.ViewHolder<Pair<Achievement, Int>>(itemView) {
            override fun onBind(with: Pair<Achievement, Int>) {
                val achievement = with.first
                val level = with.second
                itemView.achievementIconView.icon = resources.getDrawable(achievement.icon)
                itemView.achievementIconView.level = level
                itemView.achievementIconView.setOnClickListener {
                    listener?.onClickedAchievement(achievement, level, itemView.achievementIconView)
                }
            }
        }
    }
}
