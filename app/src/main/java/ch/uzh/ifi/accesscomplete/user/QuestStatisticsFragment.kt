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
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.fragment.app.commit
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.user.QuestStatisticsDao
import ch.uzh.ifi.accesscomplete.data.user.UserStore
import kotlinx.android.synthetic.main.fragment_quest_statistics.*
import javax.inject.Inject

/** Shows the user's solved quests of each type in some kind of ball pit. Clicking on each opens
 *  a QuestTypeInfoFragment that shows the quest's details. */
class QuestStatisticsFragment : Fragment(R.layout.fragment_quest_statistics),
    QuestStatisticsByQuestTypeFragment.Listener,  QuestStatisticsByCountryFragment.Listener

{
    @Inject internal lateinit var questStatisticsDao: QuestStatisticsDao
    @Inject internal lateinit var userStore: UserStore

    interface Listener {
        fun onClickedQuestType(questType: QuestType<*>, solvedCount: Int, questBubbleView: View)
        fun onClickedCountryFlag(country: String, solvedCount: Int, rank: Int?, countryBubbleView: View)
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emptyText.isGone = questStatisticsDao.getTotalAmount() != 0

        byQuestTypeButton.setOnClickListener { v -> selectorButton.check(v.id) }
        byCountryButton.setOnClickListener { v -> selectorButton.check(v.id) }

        selectorButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.byQuestTypeButton -> replaceFragment(QuestStatisticsByQuestTypeFragment())
                    R.id.byCountryButton -> replaceFragment(QuestStatisticsByCountryFragment())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (userStore.isSynchronizingStatistics) {
            emptyText.setText(R.string.stats_are_syncing)
        } else {
            emptyText.setText(R.string.quests_empty)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_FADE)
            replace(R.id.questStatisticsFragmentContainer, fragment)
        }
    }

    override fun onClickedQuestType(questType: QuestType<*>, solvedCount: Int, questBubbleView: View) {
        listener?.onClickedQuestType(questType, solvedCount, questBubbleView)
    }

    override fun onClickedCountryFlag(countryCode: String, solvedCount: Int, rank: Int?, countryBubbleView: View) {
        listener?.onClickedCountryFlag(countryCode, solvedCount, rank, countryBubbleView)
    }
}

