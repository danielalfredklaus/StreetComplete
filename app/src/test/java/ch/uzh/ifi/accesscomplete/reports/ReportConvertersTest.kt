package ch.uzh.ifi.accesscomplete.reports

import org.junit.Assert.*
import org.junit.Test



class ReportConvertersTest {

    val converter = ReportConverters()
    @Test
    fun mapToStringTest(){
        val map = mapOf(Pair("te","st"),Pair("te2","st"),Pair("te3","st"))
        val result = converter.mapToString(map)
        assertEquals("te,st,te2,st,te3,st,", result)
    }
    @Test
    fun stringToMapTest(){
        val string = "te,st,te2,st,te3,st,"
        val result = converter.stringToMap(string)
        assertEquals(mapOf(Pair("te","st"),Pair("te2","st"),Pair("te3","st")), result)
    }
    @Test
    fun mapToStringTest2(){
        val map = mapOf(Pair("te",""),Pair("te2",""),Pair("te3",""))
        val result = converter.mapToString(map)
        assertEquals("te,,te2,,te3,,", result)
    }
    @Test
    fun stringToMapTest2(){
        val string = "te,,te2,,te3,,"
        val result = converter.stringToMap(string)
        assertEquals(mapOf(Pair("te",""),Pair("te2",""),Pair("te3","")), result)
    }
    @Test
    fun listToStringTest(){
        val list = listOf("12345","6789","12345","6789")
        val result = converter.listToString(list)
        assertEquals("12345,6789,12345,6789,",result)
    }
    @Test
    fun stringToListTest(){
        val string = "12345,6789,12345,6789,"
        val result = converter.stringToList(string)
        assertEquals(listOf("12345","6789","12345","6789"),result)

    }
}
