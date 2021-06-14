package ch.uzh.ifi.accesscomplete.reports

import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.data.meta.ALL_ROADS
import ch.uzh.ifi.accesscomplete.data.meta.SURVEY_MARK_KEY
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDateString
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.quests.YesNoQuestAnswerFragment
import ch.uzh.ifi.accesscomplete.quests.construction.deleteTagsDescribingConstruction
import ch.uzh.ifi.osmapi.map.MapDataWithGeometry
import de.westnordost.osmapi.map.data.Element
import java.util.*

class VerifyReportQuestType: UzhElementQuestType<String> {

    override val commitMessage = "Determine whether the report is correct"
    override val icon = R.drawable.ic_quest_road_construction

    override fun getTitle(tags: Map<String, String>) = R.string.quest_verify_report_title

    override fun createForm() = VerifyReportAnswerFragment()

    override fun applyAnswerTo(answer: Map<String, String>, changes: StringMapChangesBuilder) {
        answer.forEach { (key, value) -> changes.addOrModify(key, value)  }
        /*
        if (answer) {
            val value = changes.getPreviousValue("construction") ?: "road"
            changes.modify("highway", value)
            deleteTagsDescribingConstruction(changes)
        } else {
            changes.addOrModify(SURVEY_MARK_KEY, Date().toCheckDateString())
        } */
    }


}
