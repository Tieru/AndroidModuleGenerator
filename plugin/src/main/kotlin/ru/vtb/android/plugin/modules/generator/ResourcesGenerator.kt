package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.utils.makeFileWithContent
import java.io.File

data class ResourcesConfig(
    val srcMainDir: String,
    val moduleName: String
)

class ResourcesGenerator(private val config: ResourcesConfig) : SimpleGenerator {

    override fun generate() {
        writeLayoutFile()
        writeValuesXml()
    }

    private fun writeLayoutFile() {
        val content = """
            <?xml version="1.0" encoding="utf-8"?>
            <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:orientation="vertical" android:layout_width="match_parent"
                android:layout_height="match_parent">
            
            </LinearLayout>
        """.trimIndent()

        val filename = "fragment_" + featureNameToFragmentFileName(config.moduleName)
        val file = File("${config.srcMainDir}/res/layout/$filename.xml")
        file.makeFileWithContent(content)
    }

    private fun writeValuesXml() {
        val content = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                
            </resources>
        """.trimIndent()

        val file = File("${config.srcMainDir}/res/values/strings.xml")
        file.makeFileWithContent(content)
    }

    private fun featureNameToFragmentFileName(featureName: String): String {
        val name = if (featureName.contains("feature")) {
            val featureNameStartIndex = featureName.indexOf("feature")
            if (featureNameStartIndex > 0) {
                featureName.substring(0, featureNameStartIndex - 1) + featureName.substring(featureNameStartIndex + 8, featureName.length)
            } else {
                featureName.substring(featureNameStartIndex + 8, featureName.length)
            }
        } else {
            featureName
        }

        return name.replace("-", "_")
    }
}
