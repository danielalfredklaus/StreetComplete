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

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/** Base task class to pull translations and other info from POEditor crowd translation platform */
abstract class AUpdateFromPOEditorTask : DefaultTask() {

    @get:Input var apiToken: String? = null

    private val baseParams get() = "api_token=$apiToken&id=97843"

    /** Fetch the localization for the given language code in the given format and do something
     *  with the contents */
    protected fun <T> fetchLocalization(languageCode: String, format: String, block: (InputStream) -> T): T {
        val url = URL(fetchLocalizationDownloadUrl(languageCode, format))
        return url.retryingQuotaConnection(null, block)
    }

    /** Fetch the download URL for the given language code. Handle quota. */
    private fun fetchLocalizationDownloadUrl(languageCode: String, format: String): String {
        return URL("https://api.poeditor.com/v2/projects/export").retryingQuotaConnection({ connection ->
            connection.doOutput = true
            connection.requestMethod = "POST"
            connection.outputStream.bufferedWriter().use { it.write(
                "$baseParams&language=${languageCode.toLowerCase(Locale.US)}&type=$format&filters=translated"
            ) }
        }) { inputStream ->
            val response = Parser.default().parse(inputStream) as JsonObject
            (response.obj("result")!!)["url"] as String
        }
    }

    /** Fetch language codes of available translations from POEditor API */
    protected fun <T> fetchLocalizations(mapping: (JsonObject) -> T): List<T> {
        return URL("https://api.poeditor.com/v2/languages/list").retryingQuotaConnection({ connection ->
            connection.doOutput = true
            connection.requestMethod = "POST"
            connection.outputStream.bufferedWriter().use { it.write(baseParams) }
        }) { inputStream ->
            val response = Parser.default().parse(inputStream) as JsonObject
            response.obj("result")!!.array<JsonObject>("languages")!!.map {
                mapping(it)
            }
        }
    }

    // this is for waiting and retrying for quota to replenish when querying POEditor API... :-|
    private fun <T> URL.retryingQuotaConnection(setup: ((HttpURLConnection) -> Unit)? = null, block: (InputStream) -> T): T {
        val maxWait = 12
        var i = 0
        while(i++ < maxWait) {
            val connection = openConnection() as HttpURLConnection
            setup?.invoke(connection)
            if (connection.responseCode == 429) {
                connection.disconnect()
                Thread.sleep(5000)
            } else {
                val result = block(connection.inputStream)
                connection.disconnect()
                return result
            }
        }
        throw Exception("POEditor API continues to report http status code 429")
    }
}
