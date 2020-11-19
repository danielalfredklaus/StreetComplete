package ch.uzh.ifi.accesscomplete.data.osm.osmquest

import ch.uzh.ifi.accesscomplete.R

class DisabledTestQuestType : TestQuestType() {
    override val defaultDisabledMessage = R.string.default_disabled_msg_go_inside
}
