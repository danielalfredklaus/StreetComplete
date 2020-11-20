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

/** Update a resources file that specifies the current translation completeness for every language */
open class UpdateAppTranslationCompletenessTask : AUpdateFromPOEditorTask() {

    @get:Input var targetFiles: ((androidResCode: String) -> String)? = null

    @TaskAction fun run() {
        val targetFiles = targetFiles ?: return

        val localizationStatus = fetchLocalizations {
            LocalizationStatus(it["code"] as String, it["percentage"] as Int)
        }
        for (status in localizationStatus) {
            val languageCode = status.languageCode
            val completedPercentage = status.completedPercentage

            val javaLanguageTag = bcp47LanguageTagToJavaLanguageTag(languageCode)
            val androidResCodes = javaLanguageTagToAndroidResCodes(javaLanguageTag)

            // create a metadata file that describes how complete the translation is
            for (androidResCode in androidResCodes) {
                // exclude default translation
                if (androidResCode == "en-rUS") continue
                val targetFile = File(targetFiles(androidResCode))
                File(targetFile.parent).mkdirs()
                targetFile.writeText("""
                    <?xml version="1.0" encoding="utf-8"?>
                    <resources>
                      <integer name="translation_completeness">${completedPercentage}</integer>
                    </resources>

                    """.trimIndent())
            }
        }
    }
}

private data class LocalizationStatus(val languageCode: String, val completedPercentage: Int)
