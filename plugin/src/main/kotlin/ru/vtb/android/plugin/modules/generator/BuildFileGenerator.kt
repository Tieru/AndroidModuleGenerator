package ru.vtb.android.plugin.modules.generator

import java.io.File
import java.lang.IllegalArgumentException

data class BuildFileConfig(
    val rootProjectDir: File,
    val templateFileRelativePath: String,
    val projectDir: File
)

class BuildFileGenerator(private val config: BuildFileConfig): SimpleGenerator {

    override fun generate() {
        val templateFile = File(config.rootProjectDir.path + "/" + config.templateFileRelativePath)
        if (!templateFile.exists()) {
            throw IllegalArgumentException("Build file template is missing. Searched in locations: '${templateFile.path}'")
        }

        println("Creating build.gradle script '${templateFile.path}'")

        val targetFile = File(config.projectDir.path + "/" + "build.gradle")
        templateFile.copyTo(targetFile)
    }
}
