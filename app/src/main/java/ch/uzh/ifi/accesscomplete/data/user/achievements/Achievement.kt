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

package ch.uzh.ifi.accesscomplete.data.user.achievements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Achievement(
    val id: String,
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    @StringRes val description: Int?,
    val condition: AchievementCondition,
    val pointsNeededToAdvanceFunction: (Int) -> Int,
    val unlockedLinks: Map<Int, List<Link>>,
    val maxLevel: Int = -1
) {
    fun getPointThreshold(level: Int): Int {
        var threshold = 0
        for(i in 0 until level) {
            threshold += pointsNeededToAdvanceFunction(i)
        }
        return threshold
    }
}

sealed class AchievementCondition

data class SolvedQuestsOfTypes(val questTypes: List<String>) : AchievementCondition()
object TotalSolvedQuests : AchievementCondition()
object DaysActive : AchievementCondition()
