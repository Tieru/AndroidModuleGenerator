package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.entity.ModuleConfig
import java.io.File

class ModuleGenerator {

    private val settings = SettingsGenerator()
    private val manifest = ManifestGenerator()
    private val packages = PackagesGenerator()

    fun generateModule(config: ModuleConfig) {
        verifyModuleNotExists(config.absolutePath)
        makeModuleDir(config.absolutePath)
        makeBuildGradle(config.templatesPath, config.buildGradlePath)

        settings.addModuleToSettings(config.project, config.relativePath)

        packages.generate(config.sourceConfig)

        manifest.generate(config.templatesPath, config.sourceConfig.manifestPath, config.sourceConfig.modulePackage)
    }

    private fun verifyModuleNotExists(modulePath: String) {
        val file = File(modulePath)
        if (file.exists()) {
            throw IllegalArgumentException("Module with path '$modulePath' already exists")
        }
    }

    private fun makeModuleDir(path: String) {
        println("Generating directory: $path")
        File(path).mkdir()
    }

    private fun makeBuildGradle(templatesPath: String, buildGradlePath: String) {
        val templateFilepath = "$templatesPath/build-template.gradle"
        val templateFile = File(templateFilepath)
        if (!templateFile.exists()) {
            println("Build file template is missing. Searched in locations: '$templateFilepath'")
            return
        }

        println("Creating build.gradle script '$templateFilepath'")

        val targetFile = File(buildGradlePath)
        templateFile.copyTo(targetFile)
    }
}
