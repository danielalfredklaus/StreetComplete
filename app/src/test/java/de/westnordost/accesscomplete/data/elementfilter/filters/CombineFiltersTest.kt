package de.westnordost.accesscomplete.data.elementfilter.filters

import de.westnordost.accesscomplete.any
import de.westnordost.accesscomplete.mock
import de.westnordost.accesscomplete.on
import org.junit.Assert.*
import org.junit.Test

class CombineFiltersTest {

    @Test fun `does not match if one doesn't match`() {
        val f1: ElementFilter = mock()
        on(f1.matches(any())).thenReturn(true)
        val f2: ElementFilter = mock()
        on(f2.matches(any())).thenReturn(false)
        assertFalse(CombineFilters(f1, f2).matches(null))
    }

    @Test fun `does match if all match`() {
        val f1: ElementFilter = mock()
        on(f1.matches(any())).thenReturn(true)
        val f2: ElementFilter = mock()
        on(f2.matches(any())).thenReturn(true)
        assertTrue(CombineFilters(f1, f2).matches(null))
    }

    @Test fun `concatenates OQL`() {
        val f1: ElementFilter = mock()
        on(f1.toOverpassQLString()).thenReturn("hell")
        val f2: ElementFilter = mock()
        on(f2.toOverpassQLString()).thenReturn("o")
        assertEquals("hello", CombineFilters(f1, f2).toOverpassQLString())
    }
}
