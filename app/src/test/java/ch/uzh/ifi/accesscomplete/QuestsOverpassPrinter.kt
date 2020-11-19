package ch.uzh.ifi.accesscomplete

import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmElementQuestType
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import ch.uzh.ifi.accesscomplete.quests.QuestModule

fun main() {

    val registry = QuestModule.questTypeRegistry(mock(), mock())

    for (questType in registry.all) {
        if (questType is OsmElementQuestType) {
            println("### " + questType.javaClass.simpleName)
            if (questType is OsmFilterQuestType) {
                val query = "[bbox:{{bbox}}];\n" + questType.filter.toOverpassQLString() + "\n out meta geom;"
                println("```\n$query\n```")
            } else {
                println("Not available, see source code")
            }
            println()
        }
    }
}
