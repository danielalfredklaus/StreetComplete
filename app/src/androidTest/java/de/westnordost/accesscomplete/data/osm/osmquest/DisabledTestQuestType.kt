package de.westnordost.accesscomplete.data.osm.osmquest

import de.westnordost.accesscomplete.R

class DisabledTestQuestType : TestQuestType() {
    override val defaultDisabledMessage = R.string.default_disabled_msg_go_inside
}
