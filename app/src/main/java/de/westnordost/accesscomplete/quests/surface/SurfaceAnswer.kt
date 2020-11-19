package de.westnordost.accesscomplete.quests.surface

abstract class AbstractSurfaceAnswer
sealed class SurfaceAnswer : AbstractSurfaceAnswer()
data class GenericSurfaceAnswer(val value : String, val note: String) : SurfaceAnswer()
data class SpecificSurfaceAnswer(val value: String) : SurfaceAnswer()
data class SidewalkSurfaceAnswer(
    var leftSidewalkAnswer: SurfaceAnswer?,
    var rightSidewalkAnswer: SurfaceAnswer?) : AbstractSurfaceAnswer()
data class SidewalkMappedSeparatelyAnswer(val value: String = "separate"): AbstractSurfaceAnswer()

