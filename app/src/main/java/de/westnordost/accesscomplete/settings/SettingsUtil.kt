package de.westnordost.accesscomplete.settings

import android.view.View
import de.westnordost.accesscomplete.data.quest.QuestType

fun genericQuestTitle(resourceProvider: View, type: QuestType<*>): String {
    // all parameters are replaced by generic three dots
    // it is assumed that quests will not have a ridiculously huge parameter count
    return resourceProvider.resources.getString(type.title, *Array(10){"…"})
}
