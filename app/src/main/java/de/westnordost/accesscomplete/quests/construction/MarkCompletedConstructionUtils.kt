package de.westnordost.accesscomplete.quests.construction

import de.westnordost.accesscomplete.data.meta.SURVEY_MARK_KEY
import de.westnordost.accesscomplete.data.osm.changes.StringMapChangesBuilder

fun deleteTagsDescribingConstruction(changes: StringMapChangesBuilder) {
    changes.deleteIfExists("construction")
    changes.deleteIfExists("source:construction")
    changes.deleteIfExists("opening_date")
    changes.deleteIfExists("source:opening_date")
    changes.deleteIfExists(SURVEY_MARK_KEY)
    changes.deleteIfExists("source:$SURVEY_MARK_KEY")
}
