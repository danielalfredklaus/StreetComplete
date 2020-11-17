package de.westnordost.streetcomplete.quests.width

abstract class AbstractWidthAnswer
data class SimpleWidthAnswer(val value: String) : AbstractWidthAnswer()
data class SidewalkWidthAnswer(
    var leftSidewalkAnswer: SimpleWidthAnswer?,
    var rightSidewalkAnswer: SimpleWidthAnswer?) : AbstractWidthAnswer()
data class SidewalkMappedSeparatelyAnswer(val value: String = "separate"): AbstractWidthAnswer()
