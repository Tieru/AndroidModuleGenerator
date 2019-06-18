package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.entity.ModuleConfig
import ru.vtb.android.plugin.modules.entity.SourceConfiguration
import org.gradle.api.Project

class ConfigGenerator {

    fun generate(project: Project, moduleName: String): ModuleConfig {
        val underscoredName = moduleName.replace("-", "_")

        val absolutePath = makeModulePath(project, underscoredName)
        val buildGradle = "$absolutePath/build.gradle"
        return ModuleConfig(
            name = moduleName,
            underscoreName = underscoredName,
            absolutePath = absolutePath,
            relativePath = makeRelativeModulePath(project, underscoredName),
            project = project,
            buildGradlePath = buildGradle,
            templatesPath = findTemplatesDir(project),
            sourceConfig = makeSourceConfiguration(absolutePath, underscoredName)
        )
    }

    private fun makeModulePath(project: Project, name: String): String {
        if (name.isBlank()) {
            throw IllegalArgumentException("No module name provided")
        }
        return "${project.projectDir}/$name"
    }

    private fun makeRelativeModulePath(project: Project, moduleName: String): String {
        return if (project == project.rootProject) {
            ":$moduleName"
        } else {
            ":${project.name}:$moduleName"
        }
    }

    private fun findTemplatesDir(project: Project): String {
        return "${project.projectDir}/tools/module-templates"
    }

    private fun makeSourceConfiguration(modulePath: String, moduleName: String): SourceConfiguration {
        val modulePackage = "ru.vtb.smb.$moduleName"
        val classesDir = "$modulePath/src/main/java/${modulePackage.replace(".", "/")}"
        val manifestPath = "$modulePath/src/main/AndroidManifest.xml"
        return SourceConfiguration(
            modulePackage,
            classesDir,
            manifestPath
        )
    }
}