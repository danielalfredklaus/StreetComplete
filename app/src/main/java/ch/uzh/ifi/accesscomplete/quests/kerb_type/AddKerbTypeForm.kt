/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
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

package ch.uzh.ifi.accesscomplete.quests.kerb_type

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestFormAnswerFragment
import ch.uzh.ifi.accesscomplete.quests.OtherAnswer
import ch.uzh.ifi.accesscomplete.quests.kerb_type.AddKerbType.Companion.NO_KERB_VALUE
import ch.uzh.ifi.accesscomplete.view.image_select.Item
import kotlinx.android.synthetic.main.quest_kerb_type.*
import kotlin.math.abs

class AddKerbTypeForm : AbstractQuestFormAnswerFragment<String>() {

    override val contentLayoutResId = R.layout.quest_kerb_type

    override val otherAnswers = listOf(
        OtherAnswer(R.string.quest_kerb_type_no_kerb) { applyAnswer(NO_KERB_VALUE) }
    )

    private val valueItems = listOf(
        Item("raised", R.drawable.kerb_raised, R.string.quest_kerb_raised, R.string.quest_kerb_raised_description, null),
        Item("lowered", R.drawable.kerb_lowered, R.string.quest_kerb_lowered, R.string.quest_kerb_lowered_description, null),
        Item("flush", R.drawable.kerb_flush, R.string.quest_kerb_flush, R.string.quest_kerb_flush_description, null))
    private val initialValueIndex = 1

    private var pagerMoved = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPager()
        initButtons()
        checkIsFormComplete()
    }

    private fun initPager() {
        pager.adapter = KerbTypePagerAdapter(requireContext(), valueItems)
        pager.setCurrentItem(initialValueIndex, false)

        pager.setPageTransformer(false) { page, position ->
            // Change scale (zoom)
            page.scaleX = 1.0F - 0.33f * abs(position)
            page.scaleY = 1.0F - 0.33f * abs(position)

            // Makes the page zoom from the center
            page.pivotX = page.width / 2.0f
            page.pivotY = page.height / 2.0f
        }

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // NOP
            }

            override fun onPageSelected(position: Int) {
                pagerMoved = true
                checkIsFormComplete()

                if (pager.currentItem >= valueItems.size - 1) {
                    nextButton.visibility = View.INVISIBLE
                } else {
                    nextButton.visibility = View.VISIBLE
                }

                if (pager.currentItem <= 0) {
                    beforeButton.visibility = View.INVISIBLE
                } else {
                    beforeButton.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // NOP
            }
        })
    }

    private fun initButtons() {
        beforeButton.setOnClickListener {
            if (pager.currentItem < 1) {
                return@setOnClickListener
            }
            pager.setCurrentItem(pager.currentItem - 1, true)
        }

        nextButton.setOnClickListener {
            if (pager.currentItem >= valueItems.size - 1) {
                return@setOnClickListener
            }
            pager.setCurrentItem(pager.currentItem + 1, true)
        }
    }

    override fun onClickOk() {
        applyAnswer(valueItems[pager.currentItem].value!!)
    }

    override fun isFormComplete(): Boolean {
        return true
    }

    override fun isRejectingClose(): Boolean {
        return pagerMoved
    }
}
