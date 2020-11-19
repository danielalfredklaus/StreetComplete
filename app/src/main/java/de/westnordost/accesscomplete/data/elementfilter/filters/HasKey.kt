package de.westnordost.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import de.westnordost.accesscomplete.data.elementfilter.quoteIfNecessary

/** key */
class HasKey(val key: String) : ElementFilter {
    override fun toOverpassQLString() = "[" + key.quoteIfNecessary() + "]"
    override fun toString() = toOverpassQLString()
    override fun matches(obj: Element?) = obj?.tags?.containsKey(key) ?: false
}
