package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.utils.makeFileWithContent
import java.io.File

data class ManifestConfig(
    val srcMainDir: String,
    val modulePackage: String,
    val packagePostfix: String? = null
)

class ManifestGenerator(val config: ManifestConfig): SimpleGenerator {

    override fun generate() {
        val content = makeManifestContent()
        writeManifest(content)
    }

    private fun makeManifestContent(): String {
        val fullPackage = config.modulePackage + config.packagePostfix
        return """
<manifest
    package="$fullPackage"/>
        """.trimIndent()
    }

    private fun writeManifest(manifest: String) {
        val file = File("${config.srcMainDir}/AndroidManifest.xml")
        file.makeFileWithContent(manifest)
    }
}
