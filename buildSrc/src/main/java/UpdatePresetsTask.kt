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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import com.beust.klaxon.Parser
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonArray
import java.io.StringWriter

/** Update the presets metadata and its translations for use with the de.westnordost:osmfeatures library */
open class UpdatePresetsTask : DefaultTask() {
    @get:Input var languageCodes: Collection<String>? = null
    @get:Input var targetDir: String? = null

    @TaskAction fun run() {
        val targetDir = targetDir ?: return
        val exportLangs = languageCodes

        // copy the presets.json 1:1
        val presetsFile = File("$targetDir/presets.json")
        presetsFile.writeText(fetchPresets())

        // download each language
        for (localizationMetadata in fetchLocalizationMetadata()) {
            val language = localizationMetadata.languageCode

            if (exportLangs != null && !exportLangs.contains(language)) continue

            println(localizationMetadata.languageCode)

            val presetsLocalization = fetchPresetsLocalizations(localizationMetadata)
            if (presetsLocalization != null) {
                val javaLanguage = bcp47LanguageTagToJavaLanguageTag(language)
                File("$targetDir/${javaLanguage}.json").writeText(presetsLocalization)
            }
        }
    }

    /** Fetch iD presets */
    private fun fetchPresets(): String {
        val presetsUrl = "https://raw.githubusercontent.com/openstreetmap/id-tagging-schema/main/dist/presets.min.json"
        return URL(presetsUrl).readText()
    }

    /** Fetch relevant meta-infos for localizations from iD */
    private fun fetchLocalizationMetadata(): List<LocalizationMetadata> {
        // this file contains a list with meta information for each localization of iD
        val contentsUrl = "https://api.github.com/repos/openstreetmap/iD/contents/dist/locales"
        val languagesJson = Parser.default().parse(URL(contentsUrl).openStream()) as JsonArray<JsonObject>

        return languagesJson.mapNotNull {
            if (it["type"] == "file") {
                val name = it["name"] as String
                val languageCode = name.subSequence(0, name.lastIndexOf(".")).toString()

                LocalizationMetadata(languageCode, it["download_url"] as String)
            } else null
        }
    }

    /** Download and pick the localization for only the presets from iD localizations
     *  (the iD localizations contain everything, such as localizations of iD UI etc)*/
    private fun fetchPresetsLocalizations(localization: LocalizationMetadata): String? {
        val localizationUrl = URL(localization.downloadUrl)
        val localizationJson = Parser.default().parse(localizationUrl.openStream()) as JsonObject
        val presetsJson = localizationJson.obj(localization.languageCode)?.obj("presets")?.obj("presets")

        return if (presetsJson != null) {
            val jsonObject = JsonObject(mapOf("presets" to presetsJson))
            jsonObject.toJsonString(true).unescapeUnicode()
        } else null
    }
}

private data class LocalizationMetadata(val languageCode: String, val downloadUrl: String)

private fun String.unescapeUnicode(): String {
    val out = StringWriter(length)
    val unicode = StringBuilder(4)
    var hadSlash = false
    var inUnicode = false

    for (ch in this) {
        if (inUnicode) {
            unicode.append(ch)
            if (unicode.length == 4) {
                val unicodeChar = unicode.toString().toInt(16).toChar()
                out.write(unicodeChar.toString())
                unicode.setLength(0)
                inUnicode = false
                hadSlash = false
            }
        } else if (hadSlash) {
            hadSlash = false
            if (ch == 'u') inUnicode = true
            else {
                out.write(92)
                out.write(ch.toString())
            }
        } else if (ch == '\\') {
            hadSlash = true
        } else {
            out.write(ch.toString())
        }
    }

    if (hadSlash) out.write(92)
    return out.toString()
}
