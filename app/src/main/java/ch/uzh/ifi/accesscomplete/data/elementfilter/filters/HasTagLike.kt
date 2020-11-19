package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.elementfilter.quoteIfNecessary

/** ~key(word)? ~ val(ue)? */
class HasTagLike(key: String, value: String) : ElementFilter {
    val key = key.toRegex()
    val value = value.toRegex()

    override fun toOverpassQLString() =
        "[" + "~" + "^(${key.pattern})$".quoteIfNecessary() + " ~ " + "^(${value.pattern})$".quoteIfNecessary() + "]"

    override fun toString() = toOverpassQLString()

    override fun matches(obj: Element?) =
        obj?.tags?.entries?.find { it.key.matches(key) && it.value.matches(value) } != null
}
