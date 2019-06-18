package ru.vtb.android.plugin.modules.generator

import java.io.File

class ManifestGenerator {

    fun generate(templatesDir: String, manifestPath: String, modulePackage: String) {
        val template = loadTemplate(templatesDir)
        val manifest = formatManifest(template, modulePackage)
        writeManifest(manifest, manifestPath)
    }

    private fun loadTemplate(templatesDir: String): String {
        val templatePath = "$templatesDir/manifest-template.xml"
        return File(templatePath).readLines().joinToString(separator = "\n")
    }

    private fun formatManifest(template: String, moduleName: String): String {
        return template.format(moduleName)
    }

    private fun writeManifest(manifest: String, manifestPath: String) {
        val file = File(manifestPath)
        file.createNewFile()
        println("Writing manifest: $manifestPath")
        file.writeText(manifest)
    }
}
