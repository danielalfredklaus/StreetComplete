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


import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

/** Update the Android string resources (translations) for all the given language codes */
open class UpdateAppTranslationsTask : AUpdateFromPOEditorTask() {

    @get:Input var languageCodes: Collection<String>? = null
    @get:Input var targetFiles: ((androidResCode: String) -> String)? = null

    @TaskAction fun run() {
        val targetFiles = targetFiles ?: return
        val exportLangs = languageCodes
            ?.map { it.toLowerCase(Locale.US) }
            // don't export en, it is the source language
            ?.filter { it != "en" }

        val languageCodes = fetchLocalizations { it["code"] as String }
        for (languageCode in languageCodes) {

            if (exportLangs == null || exportLangs.contains(languageCode.toLowerCase(Locale.US))) {
                println(languageCode)

                val javaLanguageTag = bcp47LanguageTagToJavaLanguageTag(languageCode)
                val androidResCodes = javaLanguageTagToAndroidResCodes(javaLanguageTag)

                // download the translation and save it in the appropriate directory
                val text = fetchLocalization(languageCode, "android_strings") { inputStream ->
                    inputStream.readBytes().toString(Charsets.UTF_8)
                }
                for (androidResCode in androidResCodes) {
                    File(targetFiles(androidResCode)).writeText(text)
                }
            }
        }
    }
}
