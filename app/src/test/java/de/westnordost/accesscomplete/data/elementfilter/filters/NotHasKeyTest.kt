package de.westnordost.accesscomplete.data.elementfilter.filters

import de.westnordost.accesscomplete.data.elementfilter.matches
import org.junit.Assert.*
import org.junit.Test

class NotHasKeyTest {

    @Test fun matches() {
        val f = NotHasKey("name")

        assertFalse(f.matches(mapOf("name" to "yes")))
        assertFalse(f.matches(mapOf("name" to "no")))
        assertTrue(f.matches(mapOf("neme" to "no")))
        assertTrue(f.matches(mapOf()))
    }

    @Test fun toOverpassQLString() {
        assertEquals("[!name]", NotHasKey("name").toOverpassQLString())
        assertEquals("[!'name:old']", NotHasKey("name:old").toOverpassQLString())
    }
}
