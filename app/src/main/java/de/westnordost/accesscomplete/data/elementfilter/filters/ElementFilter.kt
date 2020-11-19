package de.westnordost.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import de.westnordost.accesscomplete.data.elementfilter.Matcher

interface ElementFilter : Matcher<Element> {
    fun toOverpassQLString(): String
}
