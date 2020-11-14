package de.westnordost.streetcomplete.quests.smoothness

abstract class AbstractSmoothnessAnswer
data class SimpleSmoothnessAnswer(val value: String) : AbstractSmoothnessAnswer()
data class SidewalkSmoothnessAnswer(
    var leftSidewalkValue: SimpleSmoothnessAnswer?,
    var rightSidewalkValue: SimpleSmoothnessAnswer?) : AbstractSmoothnessAnswer()
