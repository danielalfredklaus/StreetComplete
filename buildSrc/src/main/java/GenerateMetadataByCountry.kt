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

import com.esotericsoftware.yamlbeans.YamlConfig
import com.esotericsoftware.yamlbeans.YamlReader
import com.esotericsoftware.yamlbeans.YamlWriter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.support.listFilesOrdered

import java.io.File
import java.io.StringWriter

/** generate [country code].yml files from [property].yml files, for example
 *  speedUnits.yml:
 *  DE: [miles per hour]
 *
 *  becomes
 *
 *  DE.yml:
 *  speedUnits: [miles per hour] */
open class GenerateMetadataByCountry : DefaultTask() {

    @get:Input var sourceDir: String? = null
    @get:Input var targetDir: String? = null

    @TaskAction fun run() {
        val sourceDir = sourceDir?.let { File(it) } ?: return
        val targetDir = targetDir?.let { File(it) } ?: return

        // create / clear target directory
        targetDir.mkdirs()
        targetDir.listFiles()?.forEach { it.delete() }

        val result: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()

        val ymlFiles = sourceDir.listFilesOrdered { it.isFile && it.name.endsWith(".yml") }
        ymlFiles.forEach { file ->
            val property = file.name.substringBeforeLast('.')
            println(property)
            val yaml = YamlReader(file.readText()).read() as Map<String, Any>
            for ((countryCode, value) in yaml) {
                result.getOrPut(countryCode, { mutableMapOf() })[property] = value
            }
        }

        val config = YamlConfig().apply {
            writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER)
            writeConfig.isFlowStyle = true
            writeConfig.setEscapeUnicode(false)
        }
        for ((countryCode, byProperty) in result) {
            val targetFile = File(targetDir, "$countryCode.yml")
            val fileWriter = targetFile.writer()
            fileWriter.write("# Do not edit. Source files are in /res/country_metadata\n")
            for ((property, value) in byProperty) {
                val str = StringWriter()
                val writer = YamlWriter(str, config)
                writer.write(value)
                writer.close()
                fileWriter.write("$property: $str")
            }
            fileWriter.close()
        }
    }
}
