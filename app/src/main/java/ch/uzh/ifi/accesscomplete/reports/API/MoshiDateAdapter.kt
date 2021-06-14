package ch.uzh.ifi.accesscomplete.reports.API

import android.annotation.SuppressLint
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import java.time.LocalDateTime


class MoshiDateAdapter {
    @ToJson
    fun toJson(date: LocalDateTime): String {
        return date.toString()
    }

    @SuppressLint("NewApi") //fuck api 24 and 25
    @FromJson
    fun fromJson(date: String): LocalDateTime {
        if (date.length < 10) throw JsonDataException("Unknown date: $date")
        return LocalDateTime.parse(date)
    }
}
