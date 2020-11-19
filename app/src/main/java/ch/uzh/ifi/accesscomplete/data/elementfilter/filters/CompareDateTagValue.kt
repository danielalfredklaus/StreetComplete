package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import de.westnordost.osmapi.map.data.Element
import ch.uzh.ifi.accesscomplete.data.elementfilter.quote
import ch.uzh.ifi.accesscomplete.data.elementfilter.quoteIfNecessary
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDate
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDateString
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
