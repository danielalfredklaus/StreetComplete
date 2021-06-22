package ch.uzh.ifi.accesscomplete.reports

import androidx.room.TypeConverter
import ch.uzh.ifi.accesscomplete.reports.API.*
import ch.uzh.ifi.accesscomplete.reports.database.NoIdTag
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon

class ReportConverters {
    @TypeConverter
    fun mapToString(map: Map<String, String>?): String {
        var contentString = ""
        for (pair in map!!) {
            contentString += pair.key + ","+ pair.value + ","
        }
        return contentString
    }

    @TypeConverter
    fun stringToMap(contentString: String): Map<String, String> {
        val stringArray = contentString.split(",").dropLast(1)
        val returnedMap = emptyMap<String,String>().toMutableMap()
        var i = 0
        while (i < stringArray.size) {
            returnedMap[stringArray[i]] = stringArray[i + 1]
            i += 2
        }
        return returnedMap
    }

    @TypeConverter
    fun listToString(list: List<String>): String {
        var toReturn = ""
        for(string in list){
            toReturn += "$string,"
        }
        return toReturn
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        return string.split(",").dropLast(1).toList()
    }

    @TypeConverter
    fun historyListToString(historyList: List<History>): String{
        var finalString = ""
        for(item in historyList){
                finalString += item.id + "," + item.description + "," + item.verifierid + "," + item.createdon + "," + item.imageURL + ";"
            }
        return finalString
    }

    @TypeConverter
    fun stringToHistoryList(string: String): List<History> {
        val historyStringList = string.split(";").dropLast(1)
        val historyList: MutableList<History> = mutableListOf()
        for (item in historyStringList) {
            val tempList = item.split(",")
            val history = History(tempList[0], tempList[2], tempList[4], tempList[1], tempList[3])
            historyList.add(history)
        }
        return historyList
    }

    @TypeConverter
    fun tagListToString(tagList: List<Tag>): String{
        var finalString = ""
        for(tag in tagList){
            finalString += tag.id + "," + tag.k + "," + tag.v + ";"
        }
        return finalString
    }

    @TypeConverter
    fun stringToTagList(string: String): List<Tag>{
        val tagStringList = string.split(";").dropLast(1)
        val tagList = emptyList<Tag>().toMutableList()
        for(item in tagStringList){
            val tempList = item.split(",")
            val tag = Tag(tempList[0],tempList[1],tempList[2])
            tagList.add(tag)
        }
        return tagList
    }

    @TypeConverter
    fun tagWithoutIDListToString(tagList: List<NoIdTag>): String{
        var finalString = ""
        for(tag in tagList){
            finalString += tag.k + "," + tag.v + ";"
        }
        return finalString
    }

    @TypeConverter
    fun stringToTagWithoutIDList(string: String): List<NoIdTag>{
        val tagStringList = string.split(";").dropLast(1)
        val tagList = mutableListOf<NoIdTag>()
        for(item in tagStringList){
            val tempList = item.split(",")
            val tag = NoIdTag(tempList[0],tempList[1])
            tagList.add(tag)
        }
        return tagList
    }

    @TypeConverter
    fun verifierListToString(verList: List<Verifier>): String{
        var finalString = ""
        for(ver in verList){
            finalString += ver.id + "," + ver.aid + ";"
        }
        return finalString
    }

    @TypeConverter
    fun stringToVerifierList(string: String): List<Verifier>{
        val verStringList = string.split(";").dropLast(1)
        val verList = mutableListOf<Verifier>()
        for(item in verStringList){
            val tempList = item.split(",")
            val ver = Verifier(tempList[0],tempList[1])
            verList.add(ver)
        }
        return verList
    }

    @TypeConverter
    fun latLonToString(point: LatLon): String{
        return "" + point.latitude + "," + point.longitude
    }

    @TypeConverter
    fun stringToLatLon(string: String): LatLon{
        val list = string.split(",")
        return OsmLatLon(list[0].toDouble(),list[1].toDouble())
    }

    @TypeConverter
    fun listOfLatLonToString(list: List<LatLon>): String{
        var fullString = ""
        for (item in list){
            fullString += latLonToString(item) + ";"
        }
        return fullString
    }

    @TypeConverter
    fun stringToListOfLatLong(fullString: String): List<LatLon>{
        val listOfLatLonStrings = fullString.split(";").dropLast(1)
        val finalList = mutableListOf<LatLon>()
        for(item in listOfLatLonStrings){
            finalList.add(stringToLatLon(item))
        }
        return finalList
    }

    @TypeConverter
    fun listOfDoubleToString(list: List<Double>): String {
        var toReturn = ""
        for(double in list){
            toReturn += "$double,"
        }
        return toReturn
    }

    @TypeConverter
    fun stringToListOfDouble(string: String): List<Double> {
        val doubleList = mutableListOf<Double>()
        string.split(",").dropLast(1).toList().forEach { item -> doubleList.add(item.toDouble()) }
        return doubleList
    }

}
