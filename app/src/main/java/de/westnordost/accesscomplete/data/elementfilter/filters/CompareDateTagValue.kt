package de.westnordost.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import de.westnordost.accesscomplete.data.elementfilter.quote
import de.westnordost.accesscomplete.data.elementfilter.quoteIfNecessary
import de.westnordost.accesscomplete.data.meta.toCheckDate
import de.westnordost.accesscomplete.data.meta.toCheckDateString
import java.util.*

abstract class CompareDateTagValue(val key: String, val dateFilter: DateFilter): ElementFilter {
    val date: Date get() = dateFilter.date

    override fun toOverpassQLString() : String {
        val strVal = date.toCheckDateString()
        return "[" + key.quoteIfNecessary() + "](if: date(t[" + key.quote() + "]) " + operator + " date('" + strVal + "'))"
    }

    override fun toString() = toOverpassQLString()

    override fun matches(obj: Element?): Boolean {
        val tagValue = obj?.tags?.get(key)?.toCheckDate() ?: return false
        return compareTo(tagValue)
    }

    abstract fun compareTo(tagValue: Date): Boolean
    abstract val operator: String
}
