package de.westnordost.accesscomplete.quests.traffic_signals_sound

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.data.meta.updateWithCheckDate
import de.westnordost.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import de.westnordost.accesscomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.accesscomplete.ktx.toYesNo
import de.westnordost.accesscomplete.quests.YesNoQuestAnswerFragment

class AddTrafficSignalsSound : OsmFilterQuestType<Boolean>() {

    override val elementFilter = """
        nodes with crossing = traffic_signals and highway ~ crossing|traffic_signals
        and (
          !$SOUND_SIGNALS
          or $SOUND_SIGNALS = no and $SOUND_SIGNALS older today -4 years
          or $SOUND_SIGNALS older today -8 years
        )
    """

    override val commitMessage = "Add $SOUND_SIGNALS tag"
    override val wikiLink = "Key:$SOUND_SIGNALS"
    override val icon = R.drawable.ic_quest_blind_traffic_lights_sound

    override fun getTitle(tags: Map<String, String>) = R.string.quest_traffic_signals_sound_title

    override fun createForm() = YesNoQuestAnswerFragment()

    override fun applyAnswerTo(answer: Boolean, changes: StringMapChangesBuilder) {
        changes.updateWithCheckDate(SOUND_SIGNALS, answer.toYesNo())
    }
}

private const val SOUND_SIGNALS = "traffic_signals:sound"
