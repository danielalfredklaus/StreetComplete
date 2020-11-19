package de.westnordost.accesscomplete

import de.westnordost.accesscomplete.data.osm.osmquest.OsmElementQuestType
import de.westnordost.accesscomplete.data.osm.osmquest.OsmFilterQuestType
import de.westnordost.accesscomplete.quests.QuestModule

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
