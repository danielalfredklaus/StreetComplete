package ch.uzh.ifi.accesscomplete.data.user

import ch.uzh.ifi.accesscomplete.data.ApplicationDbTestCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CountryStatisticsDaoTest : ApplicationDbTestCase() {
    private lateinit var dao: CountryStatisticsDao

    @Before
    fun createDao() {
        dao = CountryStatisticsDao(dbHelper)
    }

    @Test
    fun addAndSubtract() {
        dao.addOne("DE")
        dao.addOne("DE")
        dao.addOne("DE")
        dao.subtractOne("DE")
        assertEquals(listOf(CountryStatistics("DE", 2, null)), dao.getAll())
    }

    @Test
    fun getAllReplaceAll() {
        dao.replaceAll(listOf(
            CountryStatistics("DE", 4, null),
            CountryStatistics("NL", 1, 123)
        ))
        assertEquals(listOf(
            CountryStatistics("DE", 4, null),
            CountryStatistics("NL", 1, 123)
        ), dao.getAll())
    }
}
