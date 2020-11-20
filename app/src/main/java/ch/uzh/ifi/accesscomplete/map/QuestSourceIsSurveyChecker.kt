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

package ch.uzh.ifi.accesscomplete.map

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolygonsGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPolylinesGeometry
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestController
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestController
import ch.uzh.ifi.accesscomplete.data.quest.QuestGroup
import ch.uzh.ifi.accesscomplete.util.distanceToArcs
import javax.inject.Inject

/** Checks if the quest was solved on a survey, either by looking at the GPS position or asking
 *  the user  */
class QuestSourceIsSurveyChecker @Inject constructor(
        private val osmQuestController: OsmQuestController,
        private val osmNoteQuestController: OsmNoteQuestController
) {
    fun assureIsSurvey(context: Context, questId: Long, group: QuestGroup, locations: List<Location>, isSurveyCallback: () -> Unit) {
        if (dontShowAgain || isWithinSurveyDistance(questId, group, locations)) {
            isSurveyCallback()
        } else {
            val inner = LayoutInflater.from(context).inflate(R.layout.quest_source_dialog_layout, null, false)
            val checkBox = inner.findViewById<CheckBox>(R.id.checkBoxDontShowAgain)
            checkBox.isGone = timesShown < 1

            AlertDialog.Builder(context)
                .setTitle(R.string.quest_source_dialog_title)
                .setView(inner)
                .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ ->
                    ++timesShown
                    dontShowAgain = checkBox.isChecked
                    isSurveyCallback()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    private fun isWithinSurveyDistance(questId: Long, group: QuestGroup, locations: List<Location>): Boolean {
        val geometry = getQuestGeometry(questId, group) ?: return false
        for (location in locations) {
            val pos = OsmLatLon(location.latitude, location.longitude)
            val polyLines: List<List<LatLon>> = when (geometry) {
                is ElementPolylinesGeometry -> geometry.polylines
                is ElementPolygonsGeometry -> geometry.polygons
                else -> listOf(listOf(geometry.center))
            }
            for (polyLine in polyLines) {
                val distance = pos.distanceToArcs(polyLine)
                if (distance < location.accuracy + MAX_DISTANCE_TO_ELEMENT_FOR_SURVEY) return true
            }
        }
        return false
    }

    private fun getQuestGeometry(questId: Long, group: QuestGroup): ElementGeometry? =
        when (group) {
            QuestGroup.OSM -> osmQuestController.get(questId)?.geometry
            QuestGroup.OSM_NOTE -> osmNoteQuestController.get(questId)?.geometry
        }

    companion object {
        /*
        Considerations for choosing these values:

        - users should be encouraged to *really* go right there and check even if they think they
          see it from afar already

        - just having walked by something should though still count as survey though. (It might be
          inappropriate or awkward to stop and flip out the smartphone directly there)

        - GPS position might not be updated right after they fetched it out of their pocket, but GPS
          position should be reset to "unknown" (instead of "wrong") when switching back to the app

        - the distance is the minimum distance between the quest geometry (i.e. a road) and the line
          between the user's position when he opened the quest form and the position when he pressed
          "ok", MINUS the current GPS accuracy, so it is a pretty forgiving calculation already
        */

        private const val MAX_DISTANCE_TO_ELEMENT_FOR_SURVEY = 80f //m

        // "static" values persisted per application start
        private var dontShowAgain = false
        private var timesShown = 0
    }
}
