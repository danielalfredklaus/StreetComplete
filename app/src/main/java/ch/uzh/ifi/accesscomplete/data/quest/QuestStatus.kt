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

package ch.uzh.ifi.accesscomplete.data.quest

enum class QuestStatus {
    /** just created. AKA "open" */
    NEW,
    /** user answered the question (waiting for changes to be uploaded)  */
    ANSWERED,
    /** user chose to hide the quest. He may un-hide it later (->NEW). */
    HIDDEN,
    /** the system (decided that it) doesn't show the quest. They may become visible again (-> NEW)  */
    INVISIBLE,
    /** the quest has been uploaded (either solved or dropped through conflict). The app needs to
     * remember its solved quests for some time before deleting them so that they can be reverted
     * Note quests are generally closed after upload, they are never deleted  */
    CLOSED,
    /** the quest has been closed and after that the user chose to revert (aka undo) it. This state
     * is basically the same as CLOSED, only that it will not turn up in the list of (revertable)
     * changes. Note, that the revert-change is done via another Quest upload, this state is only
     * to mark this quest as that a revert-quest has already been created */
    REVERT; // TODO remove completely?

    val isVisible: Boolean get() = this == NEW
}
