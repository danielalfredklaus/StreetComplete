package de.westnordost.accesscomplete.data.quest

sealed class Countries

object AllCountries : Countries()
data class AllCountriesExcept(val exceptions: List<String>) : Countries() {
    constructor(vararg exceptions: String) : this(exceptions.toList())
}
data class NoCountriesExcept(val exceptions: List<String>) : Countries() {
    constructor(vararg exceptions: String) : this(exceptions.toList())
}
