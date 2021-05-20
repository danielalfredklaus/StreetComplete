package ch.uzh.ifi.accesscomplete.reports

import androidx.room.TypeConverter

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


}
