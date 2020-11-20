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

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.quest.QuestGroup
import kotlinx.android.synthetic.main.form_leave_note.*
import kotlinx.android.synthetic.main.fragment_quest_answer.*

/** Bottom sheet fragment with which the user can leave a note instead of solving the quest */
class LeaveNoteInsteadFragment : AbstractCreateNoteFragment(), IsShowingQuestDetails {

    interface Listener {
        fun onCreatedNoteInstead(questId: Long, group: QuestGroup, questTitle: String, note: String, imagePaths: List<String>?)
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    override val layoutResId = R.layout.fragment_quest_answer

    private lateinit var questTitle: String
    override var questId: Long = 0L
    override var questGroup: QuestGroup = QuestGroup.OSM

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        val args = requireArguments()
        questTitle = args.getString(ARG_QUEST_TITLE)!!
        questId = args.getLong(ARG_QUEST_ID)
        questGroup = QuestGroup.valueOf(args.getString(ARG_QUEST_GROUP)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleLabel.text = getString(R.string.map_btn_create_note)
        descriptionLabel.text = null
    }

    override fun onComposedNote(text: String, imagePaths: List<String>?) {
        listener?.onCreatedNoteInstead(questId, questGroup, questTitle, text, imagePaths)
    }

    companion object {
        private const val ARG_QUEST_TITLE = "questTitle"
        private const val ARG_QUEST_ID = "questId"
        private const val ARG_QUEST_GROUP = "questGroup"

        @JvmStatic
        fun create(questId: Long, group: QuestGroup, questTitle: String): LeaveNoteInsteadFragment {
            val f = LeaveNoteInsteadFragment()
            f.arguments = bundleOf(
                ARG_QUEST_GROUP to group.name,
                ARG_QUEST_ID to questId,
                ARG_QUEST_TITLE to questTitle
            )
            return f
        }
    }
}
