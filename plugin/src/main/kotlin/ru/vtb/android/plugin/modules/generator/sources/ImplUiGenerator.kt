package ru.vtb.android.plugin.modules.generator.sources

import ru.vtb.android.plugin.modules.entity.ModuleConfig
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.toCamelCase
import java.io.File

class ImplUiGenerator: SimpleGenerator {

    private companion object {
        const val uiDir = "ui"
    }

    override fun generate(config: ModuleConfig) {
        val fileName = makeClassName(config.name)
        val code = makeClassContent(config.sourceConfig.modulePackage, fileName)
        makeDir(config.sourceConfig.classPath + "/$uiDir")
        println("Generating 'Impl' UI Fragment: ${config.sourceConfig.classPath}/$uiDir/$fileName.kt")
        writeClass(config.sourceConfig.classPath, fileName, code)
    }

    private fun makeClassName(moduleName: String): String {
        val isFeature = moduleName.contains("feature")
        val subjectName = moduleName.toCamelCase()
            .replace("Feature", "")
            .replace("Impl", "")

        return if (isFeature) {
            "${subjectName}Feature"
        } else {
            subjectName
        }
    }

    private fun makeClassContent(pkg: String, className: String): String {
        val subjectName = className.replace("Feature", "")
        return """
            package $pkg.$uiDir

            import $pkg.presentation.${subjectName}ViewModel
            import javax.inject.Inject

            class ${subjectName}Fragment {

                @Inject
                lateinit var ${subjectName.decapitalize()}ViewModel: ${subjectName}ViewModel

            }
        """.trimIndent()
    }

    private fun writeClass(classPath: String, fileName: String, content: String) {
        val file = File("$classPath/$uiDir/$fileName.kt")
        file.createNewFile()
        file.writeText(content)
    }

    private fun makeDir(path: String) {
        val dir = File(path)
        if (dir.exists()) {
            return
        }
        dir.mkdirs()
    }
}
