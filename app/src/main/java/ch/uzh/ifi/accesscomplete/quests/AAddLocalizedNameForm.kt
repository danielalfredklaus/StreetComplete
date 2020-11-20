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

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.toObject
import ch.uzh.ifi.accesscomplete.util.AdapterDataChangedWatcher
import ch.uzh.ifi.accesscomplete.util.Serializer
import java.util.*
import javax.inject.Inject

abstract class AAddLocalizedNameForm<T> : AbstractQuestFormAnswerFragment<T>() {

    override var contentLayoutResId: Int = R.layout.quest_localizedname

    private val serializer: Serializer

    protected lateinit var adapter: AddLocalizedNameAdapter
    private lateinit var namesList: RecyclerView
    private lateinit var addLanguageButton: View

    init {
        val fields = InjectedFields()
        Injector.applicationComponent.inject(fields)
        serializer = fields.serializer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data: ArrayList<LocalizedName>? = if (savedInstanceState != null) {
            serializer.toObject(savedInstanceState.getByteArray(LOCALIZED_NAMES_DATA)!!)
        } else {
            null
        }

        initLocalizedNameAdapter(view as ViewGroup, data)
    }

    protected fun setLayout(resId: Int) {
        val view = setContentView(resId)
        initLocalizedNameAdapter(view as ViewGroup)
    }

    private fun initLocalizedNameAdapter(view: ViewGroup, data: MutableList<LocalizedName>? = null) {
        addLanguageButton = view.findViewById(R.id.addLanguageButton)
        adapter = createLocalizedNameAdapter(data.orEmpty(), addLanguageButton)
        adapter.addOnNameChangedListener { checkIsFormComplete() }
        adapter.registerAdapterDataObserver(AdapterDataChangedWatcher { checkIsFormComplete() })
        namesList = view.findViewById(R.id.namesList)
        namesList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        namesList.adapter = adapter
        namesList.isNestedScrollingEnabled = false
        checkIsFormComplete()
    }

    protected open fun createLocalizedNameAdapter(data: List<LocalizedName>, addLanguageButton: View) =
        AddLocalizedNameAdapter(data, requireContext(), getPossibleStreetsignLanguageTags(), null, null, addLanguageButton)

    protected fun getPossibleStreetsignLanguageTags(): List<String> {
        val result = mutableListOf<String>()
        result.addAll(countryInfo.officialLanguages)
        result.addAll(countryInfo.additionalStreetsignLanguages)
        return result.distinct()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val serializedNames = serializer.toBytes(ArrayList(adapter.localizedNames))
        outState.putByteArray(LOCALIZED_NAMES_DATA, serializedNames)
    }

    final override fun onClickOk() {
        onClickOk(createOsmModel())
    }

    private fun createOsmModel(): List<LocalizedName> {
        val data = adapter.localizedNames.toMutableList()
        // language is only specified explicitly in OSM (usually) if there is more than one name specified
        if(data.size == 1) {
            data[0].languageTag = ""
        }
        // but if there is more than one language, ensure that a "main" name is also specified
        else {
            val mainLanguageIsSpecified = data.indexOfFirst { it.languageTag == "" } >= 0
            // use the name specified in the top row for that
            if(!mainLanguageIsSpecified) {
                data.add(LocalizedName("", data[0].name))
            }
        }
        return data
    }

    abstract fun onClickOk(names: List<LocalizedName>)

    protected fun confirmPossibleAbbreviationsIfAny(names: Queue<String>, onConfirmedAll: () -> Unit) {
        if (names.isEmpty()) {
            onConfirmedAll()
        } else {
            /* recursively call self on confirm until the list of not-abbreviations to confirm is
               through */
            val name = names.remove()
            confirmPossibleAbbreviation(name) { confirmPossibleAbbreviationsIfAny(names, onConfirmedAll) }
        }
    }

    protected fun confirmPossibleAbbreviation(name: String, onConfirmed: () -> Unit) {
        val title = resources.getString(
            R.string.quest_streetName_nameWithAbbreviations_confirmation_title_name,
            "<i>" + Html.escapeHtml(name) + "</i>"
        ).parseAsHtml()

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(R.string.quest_streetName_nameWithAbbreviations_confirmation_description)
            .setPositiveButton(R.string.quest_streetName_nameWithAbbreviations_confirmation_positive) { _, _ -> onConfirmed() }
            .setNegativeButton(R.string.quest_generic_confirmation_no, null)
            .show()
    }

    protected fun showKeyboardInfo() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.quest_streetName_cantType_title)
            .setMessage(R.string.quest_streetName_cantType_description)
            .setPositiveButton(R.string.quest_streetName_cantType_open_settings) { _, _ ->
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
            .setNeutralButton(R.string.quest_streetName_cantType_open_store) { _, _ ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_MARKET)
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    // all added name rows are not empty
    override fun isFormComplete() = adapter.localizedNames.isNotEmpty()
            && adapter.localizedNames.all { it.name.trim().isNotEmpty() }


    class InjectedFields {
        @Inject internal lateinit var serializer: Serializer
    }

    companion object {
        private const val LOCALIZED_NAMES_DATA = "localized_names_data"
    }
}
