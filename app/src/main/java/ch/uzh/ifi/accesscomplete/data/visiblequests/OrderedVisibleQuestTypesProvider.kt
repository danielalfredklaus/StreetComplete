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

package ch.uzh.ifi.accesscomplete.data.visiblequests

import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import javax.inject.Inject

/** Provides a list of quest types that are enabled and ordered by (user chosen) importance.
 *
 *  This can be changed anytime by user preference */
class OrderedVisibleQuestTypesProvider @Inject constructor(
        private val questTypeRegistry: QuestTypeRegistry,
        private val visibleQuestTypeDao: VisibleQuestTypeDao,
        private val questTypeOrderList: QuestTypeOrderList
) {
    fun get(): List<QuestType<*>> {
        val visibleQuestTypes = questTypeRegistry.all.mapNotNull { questType ->
            questType.takeIf { visibleQuestTypeDao.isVisible(it) }
        }.toMutableList()

        questTypeOrderList.sort(visibleQuestTypes)

        return visibleQuestTypes
    }
}
/* TODO there should actually be a listener on visible quest types and the quest type order, so that
*  as a response to this, the map display (and other things) can be updated. For example, the
*  visible quest types affects any VisibleQuestListener as well. */
