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

package ch.uzh.ifi.accesscomplete.data.osmnotes

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

import ch.uzh.ifi.accesscomplete.ApplicationConstants

/** Upload and activate a list of image paths to an instance of the
 * <a href="https://github.com/exploide/sc-photo-service">StreetComplete image hosting service</a>
 */
class StreetCompleteImageUploader(private val baseUrl: String) {

    /** Upload list of images.
     *
     *  @throws ImageUploadException if there was any error */
    fun upload(imagePaths: List<String>?): List<String> {
        val imageLinks = ArrayList<String>()

        for (path in imagePaths.orEmpty()) {
            val file = File(path)
            if (!file.exists()) continue

            try {
                val connection = createConnection("upload.php")
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", URLConnection.guessContentTypeFromName(file.path))
                connection.setRequestProperty("Content-Transfer-Encoding", "binary")
                connection.setRequestProperty("Content-Length", file.length().toString())
                connection.outputStream.use { output ->
                    file.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }

                val status = connection.responseCode
                if (status == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    try {
                        val jsonResponse = JSONObject(response)
                        val url = jsonResponse.getString("future_url")
                        imageLinks.add(url)
                    } catch (e: JSONException) {
                        throw ImageUploadException("Upload Failed: Unexpected response \"$response\"")
                    }
                } else {
                    val error = connection.errorStream.bufferedReader().use { it.readText() }
                    throw ImageUploadException("Upload failed: Error code $status, Message: \"$error\"")
                }
                connection.disconnect()
            } catch (e: IOException) {
                throw ImageUploadException("Upload failed", e)
            }
        }

        return imageLinks
    }

    /** Activate the images in the given note.
     *  @throws ImageActivationException if there was any error */
    fun activate(noteId: Long) {
        try {
            val connection = createConnection("activate.php")
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "Content-Type: application/json")
            connection.outputStream.bufferedWriter().use { it.write("{\"osm_note_id\": $noteId}") }

            val status = connection.responseCode
            if (status != HttpURLConnection.HTTP_OK) {
                val error = connection.errorStream.bufferedReader().use { it.readText() }
                throw ImageActivationException("Error code $status, Message: \"$error\"")
            }
            connection.disconnect()
        } catch (e: IOException) {
            throw ImageActivationException("", e)
        }
    }

    private fun createConnection(url: String): HttpURLConnection {
        val connection = URL(baseUrl + url).openConnection() as HttpURLConnection
        connection.useCaches = false
        connection.doOutput = true
        connection.doInput = true
        connection.setRequestProperty("User-Agent", ApplicationConstants.USER_AGENT)
        return connection
    }
}

class ImageUploadException(message: String? = null, cause: Throwable? = null)
    : RuntimeException(message, cause)

class ImageActivationException(message: String? = null, cause: Throwable? = null)
    : RuntimeException(message, cause)
