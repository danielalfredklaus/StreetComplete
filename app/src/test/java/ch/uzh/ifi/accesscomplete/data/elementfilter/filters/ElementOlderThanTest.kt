package ch.uzh.ifi.accesscomplete.data.elementfilter.filters

import ch.uzh.ifi.accesscomplete.data.elementfilter.dateDaysAgo
import ch.uzh.ifi.accesscomplete.data.elementfilter.matches
import ch.uzh.ifi.accesscomplete.data.meta.toCheckDateString
import org.junit.Assert.*
import org.junit.Test

class ElementOlderThanTest {
    val c = ElementOlderThan(RelativeDate(-10f))

    @Test fun `matches older element`() {
        assertTrue(c.matches(mapOf(), dateDaysAgo(11f)))
    }

    @Test fun `does not match newer element`() {
        assertFalse(c.matches(mapOf(), dateDaysAgo(9f)))
    }

    @Test fun `does not match element from same day`() {
        assertFalse(c.matches(mapOf(), dateDaysAgo(10f)))
    }

    @Test fun `to string`() {
        val date = dateDaysAgo(10f).toCheckDateString()
        assertEquals(
            "(if: date(timestamp()) < date('$date'))",
            c.toOverpassQLString()
        )
    }
}
