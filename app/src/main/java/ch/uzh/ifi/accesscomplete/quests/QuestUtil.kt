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

import android.content.res.Resources
import android.text.Html
import android.text.Spanned
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.core.text.parseAsHtml
import de.westnordost.osmfeatures.FeatureDictionary
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import de.westnordost.osmapi.map.data.Element
import java.util.*
import java.util.concurrent.FutureTask

fun Resources.getQuestTitle(questType: QuestType<*>, element: Element?, featureDictionaryFuture: FutureTask<FeatureDictionary>?): String {
    val localeList = ConfigurationCompat.getLocales(configuration)
    val arguments = getTemplateArguments(questType, element, localeList, featureDictionaryFuture)
    return getString(getQuestTitleResId(questType, element), *arguments)
}

fun Resources.getHtmlQuestTitle(questType: QuestType<*>, element: Element?, featureDictionaryFuture: FutureTask<FeatureDictionary>?): Spanned {
    val localeList = ConfigurationCompat.getLocales(configuration)
    val arguments = getTemplateArguments(questType, element, localeList, featureDictionaryFuture)
    val spannedArguments = arguments.map {"<i>" + Html.escapeHtml(it) + "</i>"}.toTypedArray()
    return getString(getQuestTitleResId(questType, element), *spannedArguments).parseAsHtml()
}

private fun getTemplateArguments(
    questType: QuestType<*>,
    element: Element?,
    localeList: LocaleListCompat,
    featureDictionaryFuture: FutureTask<FeatureDictionary>?
): Array<String> {
    val tags = element?.tags ?: emptyMap()
    val typeName = lazy { findTypeName(tags, featureDictionaryFuture, localeList) }
    return ((questType as? OsmElementQuestType<*>)?.getTitleArgs(tags, typeName)) ?: emptyArray()
}

private fun findTypeName(
    tags: Map<String, String>,
    featureDictionaryFuture: FutureTask<FeatureDictionary>?,
    localeList: LocaleListCompat
): String? {
    featureDictionaryFuture?.get()?.let { dict ->
        localeList.forEach { locale ->
            val matches = dict.byTags(tags).forLocale(locale).find()
            if (matches.isNotEmpty()) {
                return matches.first().name
            }
        }
    }
    return null
}

private inline fun LocaleListCompat.forEach(action: (Locale) -> Unit) {
    for (i in 0 until size()) {
        action(this[i])
    }
}

private fun getQuestTitleResId(questType: QuestType<*>, element: Element?) =
    (questType as? OsmElementQuestType<*>)?.getTitle(element?.tags ?: emptyMap()) ?: questType.title
