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


import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.*
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class QuestTypeOrderListTest {
    private val one = TestQuestType()
    private val two = TestQuestType2()
    private val three = TestQuestType3()
    private val four = TestQuestType4()
    private val five = TestQuestType5()

    private lateinit var list: MutableList<QuestType<*>>
    private lateinit var questTypeOrderList: QuestTypeOrderList

    @Before fun setUpList() {
        list = mutableListOf(one, two, three, four, five)

        questTypeOrderList = QuestTypeOrderList(
            getInstrumentation().context.getSharedPreferences("Test", Context.MODE_PRIVATE),
                QuestTypeRegistry(list)
        )
        questTypeOrderList.clear()
    }

    @Test fun simpleReorder() {
        questTypeOrderList.apply(two, one)
        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(two, one)
    }

    @Test fun twoSeparateOrderLists() {
        questTypeOrderList.apply(two, one)
        questTypeOrderList.apply(five, four)

        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(two, one)
        assertThat(list).containsSequence(five, four)
    }

    @Test fun extendOrderList() {
        questTypeOrderList.apply(three, two)
        questTypeOrderList.apply(two, one)
        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(three, two, one)
    }

    @Test fun extendOrderListInReverse() {
        questTypeOrderList.apply(two, one)
        questTypeOrderList.apply(three, two)
        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(three, two, one)
    }

    @Test fun clear() {
        questTypeOrderList.apply(two, one)
        questTypeOrderList.clear()
        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(one, two)
    }

    @Test fun questTypeInOrderListButNotInToBeSortedList() {
        list.remove(three)
        questTypeOrderList.apply(three, one)
        val before = list.toList()
        questTypeOrderList.sort(list)
        assertEquals(before, list)
    }

    @Test fun questTypeInOrderListButNotInToBeSortedListDoesNotInhibitSorting() {
        list.remove(three)
        questTypeOrderList.apply(three, two)
        questTypeOrderList.apply(two, one)
        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(two, one)
    }

    @Test fun questTypeOrderIsUpdatedCorrectlyMovedDown() {
        questTypeOrderList.apply(three, two)
        questTypeOrderList.apply(two, one)
        // this now conflicts with the first statement -> should move the 3 after the 1
        questTypeOrderList.apply(one, three)

        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(two, one, three)
    }

    @Test fun questTypeOrderIsUpdatedCorrectlyMovedUp() {
        questTypeOrderList.apply(three, two)
        questTypeOrderList.apply(two, one)
        // this now conflicts with the first statement -> should move the 1 before the 2
        questTypeOrderList.apply(three, one)

        questTypeOrderList.sort(list)

        assertThat(list).containsSequence(three, one, two)
    }

    @Test fun pickQuestTypeOrders() {
        questTypeOrderList.apply(four, three)
        questTypeOrderList.apply(two, one)
        questTypeOrderList.apply(one, five)

        // merging the two here..
        questTypeOrderList.apply(one, three)

        questTypeOrderList.sort(list)
        assertThat(list).containsSequence(two, one, three, five)
    }

    @Test fun mergeQuestTypeOrders() {
        questTypeOrderList.apply(four, three)
        questTypeOrderList.apply(two, one)

        // merging the two here..
        questTypeOrderList.apply(three, two)

        questTypeOrderList.sort(list)
        assertThat(list).containsSequence(four, three, two, one)
    }

    @Test fun reorderFirstItemToBackOfSameList() {
        questTypeOrderList.apply(one, two)
        questTypeOrderList.apply(two, three)
        questTypeOrderList.apply(three, four)

        questTypeOrderList.apply(four, one)

        questTypeOrderList.sort(list)
        assertThat(list).containsSequence(two, three, four, one)
    }

    @Test fun reorderAnItemToBackOfSameList() {
        questTypeOrderList.apply(one, two)
        questTypeOrderList.apply(two, three)
        questTypeOrderList.apply(three, four)

        questTypeOrderList.apply(four, two)

        questTypeOrderList.sort(list)
        assertThat(list).containsSequence(one, three, four, two)
    }
}
