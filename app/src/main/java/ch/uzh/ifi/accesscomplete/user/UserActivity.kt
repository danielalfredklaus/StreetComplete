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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import ch.uzh.ifi.accesscomplete.FragmentContainerActivity
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.user.LoginStatusSource
import ch.uzh.ifi.accesscomplete.data.user.UserLoginStatusListener
import ch.uzh.ifi.accesscomplete.data.user.achievements.Achievement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Shows all the user information, login etc.
 *  This activity coordinates quite a number of fragments, which all call back to this one. In order
 *  of appearance:
 *  The LoginFragment, the UserFragment (which contains the viewpager with more
 *  fragments) and the "fake" dialogs AchievementInfoFragment and QuestTypeInfoFragment.
 * */
class UserActivity : FragmentContainerActivity(R.layout.activity_user),
    CoroutineScope by CoroutineScope(Dispatchers.Main),
    AchievementsFragment.Listener,
    QuestStatisticsFragment.Listener,
    UserLoginStatusListener {

    @Inject internal lateinit var loginStatusSource: LoginStatusSource

    private val countryDetailsFragment: CountryInfoFragment?
        get() = supportFragmentManager.findFragmentById(R.id.countryDetailsFragment) as CountryInfoFragment

    private val questTypeDetailsFragment: QuestTypeInfoFragment?
        get() = supportFragmentManager.findFragmentById(R.id.questTypeDetailsFragment) as QuestTypeInfoFragment

    private val achievementDetailsFragment: AchievementInfoFragment?
        get() = supportFragmentManager.findFragmentById(R.id.achievementDetailsFragment) as AchievementInfoFragment

    init {
        Injector.applicationComponent.inject(this)
    }

    /* --------------------------------------- Lifecycle --------------------------------------- */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            mainFragment = when {
                intent.getBooleanExtra(EXTRA_LAUNCH_AUTH, false) -> LoginFragment.create(true)
                loginStatusSource.isLoggedIn -> UserFragment()
                else -> LoginFragment.create()
            }
        }
        loginStatusSource.addLoginStatusListener(this)
    }

    override fun onBackPressed() {
        val countryDetailsFragment = countryDetailsFragment
        if (countryDetailsFragment != null && countryDetailsFragment.isShowing) {
            countryDetailsFragment.dismiss()
            return
        }
        val questTypeDetailsFragment = questTypeDetailsFragment
        if (questTypeDetailsFragment != null && questTypeDetailsFragment.isShowing) {
            questTypeDetailsFragment.dismiss()
            return
        }
        val achievementDetailsFragment = achievementDetailsFragment
        if (achievementDetailsFragment != null && achievementDetailsFragment.isShowing) {
            achievementDetailsFragment.dismiss()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginStatusSource.removeLoginStatusListener(this)
        coroutineContext.cancel()
    }

    /* -------------------------------- UserLoginStatusListener --------------------------------- */

    override fun onLoggedIn() {
        launch(Dispatchers.Main) {
            replaceMainFragment(UserFragment())
        }
    }

    override fun onLoggedOut() {
        launch(Dispatchers.Main) {
            replaceMainFragment(LoginFragment())
        }
    }

    /* ---------------------------- AchievementsFragment.Listener ------------------------------- */

    override fun onClickedAchievement(achievement: Achievement, level: Int, achievementBubbleView: View) {
        achievementDetailsFragment?.show(achievement, level, achievementBubbleView)
    }

    /* --------------------------- QuestStatisticsFragment.Listener ----------------------------- */

    override fun onClickedQuestType(questType: QuestType<*>, solvedCount: Int, questBubbleView: View) {
        questTypeDetailsFragment?.show(questType, solvedCount, questBubbleView)
    }

    override fun onClickedCountryFlag(country: String, solvedCount: Int, rank: Int?, countryBubbleView: View) {
        countryDetailsFragment?.show(country, solvedCount, rank, countryBubbleView)
    }

    /* ------------------------------------------------------------------------------------------ */

    private fun replaceMainFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.fade_in_from_bottom, R.anim.fade_out_to_top,
                R.anim.fade_in_from_bottom, R.anim.fade_out_to_top
            )
            replace(R.id.fragment_container, fragment)
        }
    }

    companion object {
        const val EXTRA_LAUNCH_AUTH = "ch.uzh.ifi.accesscomplete.user.launch_auth"
    }
}


