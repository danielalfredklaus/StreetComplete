package ch.uzh.ifi.accesscomplete.quests

import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapChangesBuilder
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryChange

import org.assertj.core.api.Assertions.*

fun <T> OsmElementQuestType<T>.verifyAnswer(tags:Map<String,String>, answer:T, vararg expectedChanges: StringMapEntryChange) {
    val cb = StringMapChangesBuilder(tags)
    this.applyAnswerTo(answer, cb)
    val changes = cb.create().changes
    assertThat(changes).containsExactlyInAnyOrder(*expectedChanges)
}

fun <T> OsmElementQuestType<T>.verifyAnswer(answer:T, vararg expectedChanges: StringMapEntryChange) {
    verifyAnswer(emptyMap(), answer, *expectedChanges)
}
