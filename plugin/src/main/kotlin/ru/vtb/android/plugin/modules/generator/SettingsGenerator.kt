package ru.vtb.android.plugin.modules.generator

import org.gradle.api.Project
import java.io.File

data class SettingsConfig(
    val project: Project,
    val moduleName: String
)

class SettingsGenerator(private val config: SettingsConfig) : SimpleGenerator {

    override fun generate() {
        val settingsFilePath = "${config.project.rootProject.projectDir}/settings.gradle"
        val settingsFile = File(settingsFilePath)
        assert(settingsFile.exists())

        val modulePath = makeFullModulePath()
        val moduleIncludeText = "include '$modulePath'"

        val textLines = settingsFile.readLines()
        val endOfIncludesBlock = findEndOfIncludeBlock(textLines)
        println("Writing module path into to settings.gradle at line $endOfIncludesBlock")
        val newText = textLines.toMutableList()
        newText.add(endOfIncludesBlock, moduleIncludeText)
        settingsFile.writeText(newText.joinToString(separator = "\n"))
    }

    private fun makeFullModulePath(): String {
        return config.project.path + ":" + config.moduleName
    }

    private fun findEndOfIncludeBlock(settingsFileLines: List<String>): Int {
        var lastIncludeLineIndex: Int? = null
        settingsFileLines.forEachIndexed { index: Int, line: String ->
            if (line.startsWith("include")) {
                lastIncludeLineIndex = index
            }
        }

        return lastIncludeLineIndex?.let {
            it + 1
        } ?: 0
    }
}
