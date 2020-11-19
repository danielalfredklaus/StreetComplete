package ch.uzh.ifi.accesscomplete.quests

import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryAdd
import ch.uzh.ifi.accesscomplete.data.osm.changes.StringMapEntryModify
import ch.uzh.ifi.accesscomplete.quests.foot.AddProhibitedForPedestrians
import ch.uzh.ifi.accesscomplete.quests.foot.ProhibitedForPedestriansAnswer.*
import org.junit.Test

class AddProhibitedForPedestriansTest {

    private val questType = AddProhibitedForPedestrians()

    @Test fun `apply yes answer`() {
        questType.verifyAnswer(YES, StringMapEntryAdd("foot", "no"))
    }

    @Test fun `apply no answer`() {
        questType.verifyAnswer(NO, StringMapEntryAdd("foot", "yes"))
    }

    @Test fun `apply separate sidewalk answer`() {
        questType.verifyAnswer(
            mapOf("sidewalk" to "no"),
            HAS_SEPARATE_SIDEWALK,
            StringMapEntryAdd("foot", "use_sidepath"),
            StringMapEntryModify("sidewalk", "no", "separate")
        )
    }

    @Test fun `apply living street answer`() {
        questType.verifyAnswer(
            mapOf("highway" to "residential"),
            IS_LIVING_STREET,
            StringMapEntryModify("highway", "residential", "living_street")
        )
    }
}
