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

package ch.uzh.ifi.accesscomplete.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.getYamlObject
import kotlinx.android.synthetic.main.fragment_credits.*
import org.sufficientlysecure.htmltextview.HtmlTextView

/** Shows the credits of this app */
class CreditsFragment : Fragment(R.layout.fragment_credits) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addContributorsTo(readAuthors(), authors)
        addContributorsTo(readMainContributors(), mainCredits)
        addContributorsTo(readProjectsContributors(), projectsCredits)
        addContributorsTo(readCodeContributors(), codeCredits)
        addContributorsTo(readArtContributors(), artCredits)

        val inflater = LayoutInflater.from(view.context)
        for ((language, translators) in readTranslators()) {
            val item = inflater.inflate(R.layout.row_credits_translators, translationCredits, false)
            (item.findViewById<View>(R.id.language) as TextView).text = language
            (item.findViewById<View>(R.id.contributors) as TextView).text = translators
            translationCredits.addView(item)
        }

        val translationCreditsMore = view.findViewById<HtmlTextView>(R.id.translationCreditsMore)
        translationCreditsMore.setHtml(getString(R.string.credits_translations))
        val contributorMore = view.findViewById<HtmlTextView>(R.id.contributorMore)
        contributorMore.setHtml(getString(R.string.credits_contributors))
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.about_title_authors)
    }

    private fun addContributorsTo(contributors: List<String>, view: ViewGroup) {
        val items = contributors.joinToString("") { "<li>$it</li>" }
        val textView = HtmlTextView(activity)
        TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_Body)
        textView.setTextIsSelectable(true)
        textView.setHtml("<ul>$items</ul>")
        view.addView(textView, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
    }

    private fun readAuthors() = resources.getYamlObject<List<String>>(R.raw.credit_authors)

    private fun readMainContributors() = resources.getYamlObject<List<String>>(R.raw.credits_main)

    private fun readProjectsContributors() = resources.getYamlObject<List<String>>(R.raw.credits_projects)

    private fun readCodeContributors() =
        resources.getYamlObject<List<String>>(R.raw.credits_code) + getString(R.string.credits_and_more)

    private fun readArtContributors() = resources.getYamlObject<List<String>>(R.raw.credits_art)

    private fun readTranslators() =
        resources.getYamlObject<LinkedHashMap<String, String>>(R.raw.credits_translations)
}
