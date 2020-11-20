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

import java.io.StringWriter
import java.util.*



// Java (and thus also Android) uses some old iso (language) codes. F.e. id -> in etc.
// so the localized files also need to use the old iso codes
fun bcp47LanguageTagToJavaLanguageTag(bcp47: String): String {
    val locale = Locale.forLanguageTag(bcp47)
    var result = locale.language
    if (locale.script.isNotEmpty()) result += "-" + locale.script
    if (locale.country.isNotEmpty()) result += "-" + locale.country
    return result
}

fun javaLanguageTagToAndroidResCodes(languageTag: String): List<String> {
    val locale = Locale.forLanguageTag(languageTag)
    // scripts not supported by Android resource system
    if (locale.script.isNotEmpty()) return listOf()

    if (languageTag == "nb")    return listOf("no", "nb")
    if (languageTag == "zh-CN") return listOf("zh")
    val withCountry = Regex("([a-z]{2,3})-([A-Z]{2})").matchEntire(languageTag)
    if (withCountry != null) {
        return listOf(withCountry.groupValues[1] + "-r" + withCountry.groupValues[2])
    }
    return listOf(languageTag)
}
