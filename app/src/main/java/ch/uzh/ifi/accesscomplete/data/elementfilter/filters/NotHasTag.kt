package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.elementfilter.quoteIfNecessary

/** key != value */
class NotHasTag(val key: String, val value: String) : ElementFilter {
    override fun toOverpassQLString() = "[" + key.quoteIfNecessary() + " != " + value.quoteIfNecessary() + "]"
    override fun toString() = toOverpassQLString()
    override fun matches(obj: Element?) = obj?.tags?.get(key) != value
}
