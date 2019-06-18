package ru.vtb.android.plugin.modules.generator.sources

import ru.vtb.android.plugin.modules.entity.ModuleConfig
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.toCamelCase
import java.io.File

class ImplDIGenerator: SimpleGenerator {

    private companion object {
        const val diDir = "di"
    }

    override fun generate(config: ModuleConfig) {
        val className = makeClassName(config.name)

        makeDir(config.sourceConfig.classPath + "/$diDir")
        writeFeatureComponent(className, config.sourceConfig.modulePackage, config.sourceConfig.classPath)
        writeFeatureModule(className, config.sourceConfig.modulePackage, config.sourceConfig.classPath)
        writeScreenComponent(className, config.sourceConfig.modulePackage, config.sourceConfig.classPath)
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

    private fun writeFeatureComponent(className: String, pkg: String, classPath: String) {
        val component = makeFeatureComponentClass(pkg, className)
        println("Generating 'Impl' Feature Component: $classPath/$diDir/${className}Component.kt")
        writeClass("$classPath/$diDir", className + "Component", component)
    }

    private fun makeFeatureComponentClass(pkg: String, className: String): String {
        val apiInterface = className + "Api"
        return """
            package $pkg.$diDir

            import dagger.Component
            import ru.vtb.smb.core.PerFeature
            import $pkg.$apiInterface

            @Component
            @PerFeature
            abstract class ${className}Component : $apiInterface {

            }
        """.trimIndent()
    }

    private fun writeFeatureModule(className: String, pkg: String, classPath: String) {
        val component = makeFeatureModuleClass(pkg, className)
        println("Generating 'Impl' Feature Module: $classPath/$diDir/${className}Module.kt")
        writeClass("$classPath/$diDir", className + "Module", component)
    }

    private fun makeFeatureModuleClass(pkg: String, className: String): String {
        val subjectName = className.replace("Feature", "")
        return """
            package $pkg.$diDir

            import dagger.Module
            import dagger.Provides
            import ru.vtb.smb.core.PerScreen
            import $pkg.presentation.${subjectName}ViewModel

            @Module
            class ${className}Module {

            }

            @Module
            class ${subjectName}ScreenModule {
                @PerScreen
                @Provides
                fun get${subjectName}ViewModel(): ${subjectName}ViewModel {
                    return ${subjectName}ViewModel(resultCallback)
                }
            }
        """.trimIndent()
    }

    private fun writeScreenComponent(className: String, pkg: String, classPath: String) {
        val component = makeScreenComponentClass(pkg, className)
        println("Generating 'Impl' Screen Component: $classPath/$diDir/${className}ScreenComponent.kt")
        writeClass("$classPath/$diDir", className + "ScreenComponent", component)
    }

    private fun makeScreenComponentClass(pkg: String, className: String): String {
        val subjectName = className.replace("Feature", "")
        return """
            package $pkg.$diDir

            import dagger.Subcomponent
            import ru.vtb.smb.core.PerScreen
            import $pkg.ui.EnterCodeFragment

            @Subcomponent(modules = [${subjectName}ScreenModule::class])
            @PerScreen
            interface ${subjectName}ScreenComponent {

            }
        """.trimIndent()
    }

    private fun makeDir(path: String) {
        val dir = File(path)
        if (dir.exists()) {
            return
        }
        dir.mkdirs()
    }

    private fun writeClass(classPath: String, fileName: String, content: String) {
        val file = File("$classPath/$fileName.kt")
        file.createNewFile()
        file.writeText(content)
    }
}
