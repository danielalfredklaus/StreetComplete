package de.westnordost.accesscomplete.data.elementfilter.filters

/** key > value */
class HasTagGreaterThan(key: String, value: Float): CompareTagValue(key, value) {
    override val operator = ">"
    override fun compareTo(tagValue: Float) = tagValue > value
}
