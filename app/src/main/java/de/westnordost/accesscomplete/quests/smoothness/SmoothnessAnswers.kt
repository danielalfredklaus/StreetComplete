package de.westnordost.accesscomplete.quests.smoothness

abstract class AbstractSmoothnessAnswer
data class SimpleSmoothnessAnswer(val value: String) : AbstractSmoothnessAnswer()
data class SidewalkSmoothnessAnswer(
    var leftSidewalkAnswer: SimpleSmoothnessAnswer?,
    var rightSidewalkAnswer: SimpleSmoothnessAnswer?) : AbstractSmoothnessAnswer()
data class SidewalkMappedSeparatelyAnswer(val value: String = "separate"): AbstractSmoothnessAnswer()
