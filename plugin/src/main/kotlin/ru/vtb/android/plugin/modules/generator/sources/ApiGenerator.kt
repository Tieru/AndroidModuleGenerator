package ru.vtb.android.plugin.modules.generator.sources

import ru.vtb.android.plugin.modules.entity.ModuleConfig
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.toCamelCase
import java.io.File

class ApiGenerator : SimpleGenerator {

    override fun generate(config: ModuleConfig) {
        val fileName = makeClassName(config.name)
        val code = makeClassContent(config.sourceConfig.modulePackage, fileName)
        println("Generating 'Api' Component: ${config.sourceConfig.classPath}/$fileName.kt")
        writeClass(config.sourceConfig.classPath, fileName, code)
    }

    private fun makeClassName(moduleName: String): String {
        val isFeature = moduleName.contains("feature")
        val subjectName = moduleName.toCamelCase()
            .replace("Feature", "")
            .replace("Api", "")

        return if (isFeature) {
            "${subjectName}Feature"
        } else {
            subjectName
        }
    }

    private fun makeClassContent(pkg: String, fileName: String): String {
        return """
            "package $pkg

            interface ${fileName}Api {

            }

            interface ${fileName}Dependencies {

            }

        """.trimIndent()
    }


    private fun writeClass(classPath: String, fileName: String, content: String) {
        val file = File("$classPath/$fileName.kt")
        file.createNewFile()
        file.writeText(content)
    }
}
