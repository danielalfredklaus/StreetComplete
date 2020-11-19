package de.westnordost.accesscomplete.quests.sidewalk

sealed class SidewalkAnswer
data class SidewalkSides(val left:Boolean, val right:Boolean) : SidewalkAnswer()
object SeparatelyMapped: SidewalkAnswer()
