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

package ch.uzh.ifi.accesscomplete.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ch.uzh.ifi.accesscomplete.BuildConfig
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesDao
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuest
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.ktx.toast
import kotlinx.coroutines.*
import javax.inject.Inject

/** Shows the settings screen */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    @Inject internal lateinit var prefs: SharedPreferences
    @Inject internal lateinit var downloadedTilesDao: DownloadedTilesDao
    @Inject internal lateinit var osmQuestController: OsmQuestController
    @Inject internal lateinit var osmNoteQuestController: OsmNoteQuestController
    @Inject internal lateinit var resurveyIntervalsUpdater: ResurveyIntervalsUpdater

    interface Listener {
        fun onClickedQuestSelection()
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        PreferenceManager.setDefaultValues(requireContext(), R.xml.preferences, false)
        addPreferencesFromResource(R.xml.preferences)

        findPreference<Preference>("quests")?.setOnPreferenceClickListener {
            listener?.onClickedQuestSelection()
            true
        }

        findPreference<Preference>("quests.invalidation")?.setOnPreferenceClickListener {
            context?.let {
                AlertDialog.Builder(it)
                    .setMessage(R.string.invalidation_dialog_message)
                    .setPositiveButton(R.string.invalidate_confirmation) { _, _ ->
                        downloadedTilesDao.removeAll()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
            true
        }

        findPreference<Preference>("quests.restore.hidden")?.setOnPreferenceClickListener {
            val hidden = osmQuestController.unhideAll()
            context?.toast(getString(R.string.restore_hidden_success, hidden), Toast.LENGTH_LONG)
            true
        }

        findPreference<Preference>("debug")?.isVisible = BuildConfig.DEBUG

        findPreference<Preference>("debug.quests")?.setOnPreferenceClickListener {
            startActivity(Intent(context, ShowQuestFormsActivity::class.java))
            true
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.action_settings)
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when(key) {
            Prefs.SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS -> {
                val preference = preferenceScreen.findPreference<Preference>(Prefs.SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS) ?: return
                launch {
                    preference.isEnabled = false
                    applyNoteVisibility()
                    preference.isEnabled = true
                }
            }
            Prefs.AUTOSYNC -> {
                if (Prefs.Autosync.valueOf(prefs.getString(Prefs.AUTOSYNC, "ON")!!) != Prefs.Autosync.ON) {
                    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_tutorial_upload, null)
                    AlertDialog.Builder(requireContext())
                        .setView(view)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
            }
            Prefs.THEME_SELECT -> {
                val theme = Prefs.Theme.valueOf(prefs.getString(Prefs.THEME_SELECT, "AUTO")!!)
                AppCompatDelegate.setDefaultNightMode(theme.appCompatNightMode)
                activity?.recreate()
            }
            Prefs.RESURVEY_INTERVALS -> {
                resurveyIntervalsUpdater.update()
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is DialogPreferenceCompat) {
            val fragment = preference.createDialog()
            fragment.arguments = bundleOf("key" to preference.key)
            fragment.setTargetFragment(this, 0)
            fragment.show(parentFragmentManager, "androidx.preference.PreferenceFragment.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    private suspend fun applyNoteVisibility() = withContext(Dispatchers.IO) {
        val showNonQuestionNotes = prefs.getBoolean(Prefs.SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS, false)
        if (showNonQuestionNotes) {
            osmNoteQuestController.makeAllInvisibleVisible()
        } else {
            val hideQuests = mutableListOf<OsmNoteQuest>()
            for (quest in osmNoteQuestController.getAllVisible()) {
                if (!quest.probablyContainsQuestion()) {
                    hideQuests.add(quest)
                }
            }
            osmNoteQuestController.makeAllInvisible(hideQuests)
        }
    }
}
