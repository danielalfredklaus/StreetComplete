/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.data.user

import ch.uzh.ifi.accesscomplete.ApplicationConstants
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/** Downloads statistics from the backend */
class StatisticsDownloader(private val baseUrl: String) {

    private val lastActivityDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)

    fun download(osmUserId: Long): Statistics {
        (URL("$baseUrl?user_id=$osmUserId").openConnection() as HttpURLConnection).run {
            useCaches = false
            doOutput = true
            doInput = true
            setRequestProperty("User-Agent", ApplicationConstants.USER_AGENT)
            requestMethod = "GET"
            when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    return parse(inputStream.bufferedReader().use { it.readText() })
                }
                else -> {
                    val errorMessage = responseMessage
                    val errorDescription = errorStream?.bufferedReader()?.use { it.readText() }
                    throw IOException("$responseCode $errorMessage: $errorDescription")
                }
            }
        }
    }

    private fun parse(json: String): Statistics {
        val obj = JSONObject(json)
        val questTypesJson = obj.getJSONObject("questTypes")
        val questTypes: MutableMap<String, Int> = mutableMapOf()
        for (questType in questTypesJson.keys()) {
            questTypes[questType] = questTypesJson.getInt(questType)
        }
        val countriesJson = obj.getJSONObject("countries")
        val countries: MutableMap<String, Int> = mutableMapOf()
        for (country in countriesJson.keys()) {
            countries[country] = countriesJson.getInt(country)
        }
        val countryRanksJson = obj.getJSONObject("countryRanks")
        val countryRanks: MutableMap<String, Int> = mutableMapOf()
        for (country in countryRanksJson.keys()) {
            countryRanks[country] = countryRanksJson.getInt(country)
        }
        val countriesStatistics = countries.map { CountryStatistics(it.key, it.value, countryRanks[it.key]) }
        val rank = obj.getInt("rank")
        val daysActive = obj.getInt("daysActive")
        val isAnalyzing = obj.getBoolean("isAnalyzing")
        val lastUpdate = lastActivityDateFormat.parse(obj.getString("lastUpdate"))!!
        return Statistics(questTypes, countriesStatistics, rank, daysActive, lastUpdate.time, isAnalyzing)
    }
}
