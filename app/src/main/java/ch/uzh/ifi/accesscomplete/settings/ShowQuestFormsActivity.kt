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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import de.westnordost.osmapi.map.data.OsmWay
import ch.uzh.ifi.accesscomplete.Injector
import ch.uzh.ifi.accesscomplete.Prefs
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.data.quest.*
import ch.uzh.ifi.accesscomplete.quests.AbstractQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.reports.UzhElementQuestType
import ch.uzh.ifi.accesscomplete.view.ListAdapter
import kotlinx.android.synthetic.main.fragment_show_quest_forms.*
import kotlinx.android.synthetic.main.row_quest_display.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import javax.inject.Inject

/** activity only used in debug, to show all the different forms for the different quests. */
class ShowQuestFormsActivity : AppCompatActivity(), AbstractQuestAnswerFragment.Listener {

    @Inject internal lateinit var questTypeRegistry: QuestTypeRegistry
    @Inject internal lateinit var prefs: SharedPreferences

    private val showQuestFormAdapter: ShowQuestFormAdapter = ShowQuestFormAdapter()

    private var currentQuestType: QuestType<*>? = null

    init {
        Injector.applicationComponent.inject(this)

        val questTypes = questTypeRegistry.all.toMutableList()

        // Filter out note quest (relies on notes in the DB and will crash here...)
        showQuestFormAdapter.list = questTypes.filter { it !is OsmNoteQuestType && it !is UzhElementQuestType }.toMutableList()
    } //TODO: Either change filter to make sure it is neither notes or UZH use this class to help
        // me implement the uzh quests properly.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_show_quest_forms)
        toolbar.navigationIcon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_close_white_24dp)
        toolbar.navigationContentDescription = resources.getString(R.string.close)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.title = "Show Quest Forms"

        questFormContainer.setOnClickListener { onBackPressed() }

        showQuestFormsList.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = showQuestFormAdapter
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            popQuestForm()
        } else {
            super.onBackPressed()
        }
    }

    private fun popQuestForm() {
        questFormContainer.visibility = View.GONE
        supportFragmentManager.popBackStack()
        currentQuestType = null
    }

    inner class ShowQuestFormAdapter: ListAdapter<QuestType<*>>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder<QuestType<*>> =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_quest_display, parent, false))

        private inner class ViewHolder(itemView: View) : ListAdapter.ViewHolder<QuestType<*>>(itemView) {
            override fun onBind(with: QuestType<*>) {
                itemView.questIcon.setImageResource(with.icon)
                itemView.questTitle.text = genericQuestTitle(itemView, with)
                itemView.setOnClickListener { onClickQuestType(with) }
            }
        }
    }

    private fun onClickQuestType(questType: QuestType<*>) {
        val latititudeDelta = 0
        val longitudeDelta = 0
        val firstLat = Double.fromBits(prefs.getLong(Prefs.MAP_LATITUDE, 0.0.toBits()))
        val firstLng = Double.fromBits(prefs.getLong(Prefs.MAP_LONGITUDE, 0.0.toBits()))
        val firstPos = OsmLatLon(firstLat, firstLng)
        val secondLat = Double.fromBits(prefs.getLong(Prefs.MAP_LATITUDE, (0.0 + latititudeDelta).toBits()))
        val secondLng = Double.fromBits(prefs.getLong(Prefs.MAP_LONGITUDE, (0.0 + longitudeDelta).toBits()))
        val secondPos = OsmLatLon(secondLat, secondLng)
        val centerLat = Double.fromBits(prefs.getLong(Prefs.MAP_LATITUDE, (0.0 + latititudeDelta/2).toBits()))
        val centerLng = Double.fromBits(prefs.getLong(Prefs.MAP_LONGITUDE, (0.0 + longitudeDelta/2).toBits()))
        val centerPos = OsmLatLon(centerLat, centerLng)
        val tags =  mapOf("highway" to "cycleway", "name" to "<object name>")
        val element = OsmWay(1, 1, mutableListOf(1, 2), tags)
        val elementGeometry = ElementPolylinesGeometry(listOf(listOf(firstPos, secondPos)), centerPos)

        val quest = object : Quest {
            override var id: Long? = 1L
            override val center = firstPos
            override val markerLocations = listOf<LatLon>(firstPos)
            override val geometry = elementGeometry
            override val type = questType
            override var status = QuestStatus.NEW
            override val lastUpdate = Date()
        }

        val f = questType.createForm()
        val args = AbstractQuestAnswerFragment.createArguments(quest, QuestGroup.OSM, element, 0f, 0f)
        f.arguments = args

        currentQuestType = questType

        questFormContainer.visibility = View.VISIBLE
        supportFragmentManager.commit {
            replace(R.id.questForm, f)
            addToBackStack(null)
        }
    }

    override fun onAnsweredQuest(questId: Long, group: QuestGroup, answer: Any) {
        val builder = StringMapChangesBuilder(mapOf())
        (currentQuestType as? OsmElementQuestType<Any>)?.applyAnswerTo(answer, builder)
        val tagging = builder.create().changes.joinToString("\n")
        AlertDialog.Builder(this)
            .setMessage("Tagging\n$tagging")
            .show()
        popQuestForm()
    }
    override fun onComposeNote(questId: Long, group: QuestGroup, questTitle: String) {
        popQuestForm()
        AlertDialog.Builder(this)
            .setMessage("Composing note")
            .show()
    }
    override fun onSplitWay(osmQuestId: Long) {
        popQuestForm()
        AlertDialog.Builder(this)
            .setMessage("Splitting way")
            .show()
    }
    override fun onSkippedQuest(questId: Long, group: QuestGroup) {
        popQuestForm()
        AlertDialog.Builder(this)
            .setMessage("Skipping quest")
            .show()
    }

    override fun onHighlightSidewalkSide(questId: Long, group: QuestGroup, side: AbstractQuestAnswerFragment.Listener.SidewalkSide) {
        // NOP
    }
}
