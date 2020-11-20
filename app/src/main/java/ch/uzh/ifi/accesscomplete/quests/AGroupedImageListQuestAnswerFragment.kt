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

package ch.uzh.ifi.accesscomplete.quests

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.core.view.postDelayed
import androidx.preference.PreferenceManager

import javax.inject.Inject

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.view.image_select.GroupableDisplayItem
import ch.uzh.ifi.accesscomplete.view.image_select.GroupedImageSelectAdapter
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.quest_generic_list.*
import java.util.*
import kotlin.math.max

/**
 * Abstract class for quests with a grouped list of images and one to select.
 *
 * Saving and restoring state is not implemented
 */
abstract class AGroupedImageListQuestAnswerFragment<I,T> : AbstractQuestFormAnswerFragment<T>() {

    override val contentLayoutResId = R.layout.quest_generic_list

    protected lateinit var imageSelector: GroupedImageSelectAdapter<I>

    protected abstract val allItems: List<GroupableDisplayItem<I>>
    protected abstract val topItems: List<GroupableDisplayItem<I>>

    @Inject internal lateinit var favs: LastPickedValuesStore<I>

    private val selectedItem get() = imageSelector.selectedItem

    protected open val itemsPerRow = 3

    override fun onAttach(ctx: Context) {
        super.onAttach(ctx)
        favs = LastPickedValuesStore(PreferenceManager.getDefaultSharedPreferences(ctx.applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageSelector = GroupedImageSelectAdapter(GridLayoutManager(activity, itemsPerRow))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.layoutManager = imageSelector.gridLayoutManager
        list.isNestedScrollingEnabled = false

        showMoreButton.setOnClickListener {
            imageSelector.items = allItems
            showMoreButton.visibility = View.GONE
        }

        selectHintLabel.setText(R.string.quest_select_hint_most_specific)

        imageSelector.listeners.add { checkIsFormComplete() }
        imageSelector.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                scrollTo(positionStart)
            }
        })
        checkIsFormComplete()

        imageSelector.items = getInitialItems()
        list.adapter = imageSelector
    }

    private fun scrollTo(index: Int) {
        val item = imageSelector.gridLayoutManager.getChildAt(max(0, index - 1))
        if (item != null) {
            val itemPos = IntArray(2)
            item.getLocationInWindow(itemPos)
            val scrollViewPos = IntArray(2)
            scrollView.getLocationInWindow(scrollViewPos)

            scrollView.postDelayed(250) {
                scrollView.smoothScrollTo(0, itemPos[1] - scrollViewPos[1])
            }
        }
    }

    private fun getInitialItems(): List<GroupableDisplayItem<I>> {
        val items = LinkedList(topItems)
        favs.moveLastPickedGroupableDisplayItemToFront(javaClass.simpleName, items, allItems)
        return items
    }

    override fun onClickOk() {
        val item = selectedItem!!
        val itemValue = item.value

        if (itemValue == null) {
            context?.let {
                AlertDialog.Builder(it)
                .setMessage(R.string.quest_generic_item_invalid_value)
                .setPositiveButton(android.R.string.ok, null)
                .show()
            }
        } else {
            if (item.isGroup) {
                context?.let {
                    AlertDialog.Builder(it)
                        .setMessage(R.string.quest_generic_item_confirmation)
                        .setNegativeButton(R.string.quest_generic_confirmation_no, null)
                        .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ ->
                            favs.add(javaClass.simpleName, itemValue)
                            onClickOk(itemValue)
                        }
                        .show()
                }
            }
            else {
                favs.add(javaClass.simpleName, itemValue)
                onClickOk(itemValue)
            }
        }
    }

    abstract fun onClickOk(value: I)

    override fun isFormComplete() = selectedItem != null
}
