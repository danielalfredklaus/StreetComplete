package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.elementfilter.Matcher

interface ElementFilter : Matcher<Element> {
    fun toOverpassQLString(): String
}
