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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TAB_LABEL_VISIBILITY_LABELED
import com.google.android.material.tabs.TabLayout.TAB_LABEL_VISIBILITY_UNLABELED
import com.google.android.material.tabs.TabLayoutMediator
import ch.uzh.ifi.accesscomplete.HasTitle
import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.fragment_user.*

/** Shows the viewpager with the user profile, user statistics, achievements and links */
class UserFragment : Fragment(R.layout.fragment_user), HasTitle {

    override val title: String get() = getString(R.string.user_profile)

    /* --------------------------------------- Lifecycle --------------------------------------- */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount() = PAGES.size
            override fun createFragment(position: Int) = PAGES[position].creator()
        }
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.tabMode = TabLayout.MODE_FIXED
        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            val page = PAGES[position]
            tab.setIcon(page.icon)
            tab.setText(page.title)
            tab.tabLabelVisibility =
                if (resources.getBoolean(R.bool.show_user_tabs_text)) TAB_LABEL_VISIBILITY_LABELED
                else TAB_LABEL_VISIBILITY_UNLABELED
        }.attach()
    }

    private data class Page(@StringRes val title: Int, @DrawableRes val icon: Int, val creator: () -> Fragment)


    companion object {
        private val PAGES = listOf(
            Page(R.string.user_profile_title, R.drawable.ic_profile_white_48dp) {  ProfileFragment() },
            Page(R.string.user_quests_title, R.drawable.ic_star_white_48dp) { QuestStatisticsFragment() },
            Page(R.string.user_achievements_title, R.drawable.ic_achievements_white_48dp) { AchievementsFragment() },
            Page(R.string.user_links_title, R.drawable.ic_bookmarks_white_48dp) { LinksFragment() }
        )
    }
}
