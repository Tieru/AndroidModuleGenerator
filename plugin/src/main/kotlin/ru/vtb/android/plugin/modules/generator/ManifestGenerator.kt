package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.utils.makeFileWithContent
import java.io.File

data class ManifestConfig(
    val srcMainDir: String,
    val modulePackage: String
)

class ManifestGenerator(val config: ManifestConfig): SimpleGenerator {

    override fun generate() {
        val content = makeManifestContent()
        writeManifest(content)
    }

    private fun makeManifestContent(): String {
        return """
<manifest
    package="${config.modulePackage}"/>
        """.trimIndent()
    }

    private fun writeManifest(manifest: String) {
        val file = File("${config.srcMainDir}/AndroidManifest.xml")
        file.makeFileWithContent(manifest)
    }
}
