package ru.vtb.android.plugin.modules.config

import org.gradle.api.Project
import ru.vtb.android.plugin.modules.entity.*
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.toCamelCase
import java.io.File

abstract class BasicModuleConfigGenerator {

    abstract fun makeModuleType(): ModuleType

    /**
     * Добавляет к названию модуля нужный постфикс, отделенный от названия разделителем
     */
    protected fun makeModuleName(featureName: String, postfix: String?, moduleNameDelimiter: String): String {
        return postfix?.let { featureName + moduleNameDelimiter + it } ?: featureName
    }

    /**
     * Создает конфигурацию текущего модуля: название модуля, название фичи, пути к сорцам и т.д.
     */
    protected fun makeSourcesConfig(
        project: Project,
        featureName: String,
        moduleName: String,
        addPostfixToPackageName: Boolean,
        naming: ProjectNamingOptions
    ): SourceConfiguration {
        val packageNameRaw = if (addPostfixToPackageName) moduleName else featureName
        val modulePackage = makeModulePackage(packageNameRaw, naming)

        val moduleNameModifiers = mutableListOf<ModuleNameModifier>()
        if (moduleName.toLowerCase().contains("feature")) {
            moduleNameModifiers.add(Feature)
        }

        val fullFeatureName = featureName.toCamelCase()
        val cleanFeatureName = fullFeatureName.replace("Feature", "")

        val moduleDirPath = "${project.projectDir}/$moduleName"

        return SourceConfiguration(
            moduleName,
            modulePackage,
            File(moduleDirPath),
            File("$moduleDirPath/src/main"),
            File("$moduleDirPath/src/main/java"),
            cleanFeatureName,
            fullFeatureName,
            moduleNameModifiers
        )
    }

    /**
     * Преобразует имя модуля и пакет приложения в пакет модуля
     *
     * @param moduleName строка с названием модуля из параметров gradle команды (например, 'feature_best_module')
     * @param naming предоставляет delimiter на который нужно заменить знаки "-", "_" в названии модуля,
     *               а также список слов, которые нужно исключить из названия модуля
     */
    private fun makeModulePackage(moduleName: String, naming: ProjectNamingOptions): String {
        var modulePackage = moduleName
        for (wordToOmit in naming.omitWordsInPackage) {
            modulePackage = modulePackage.replace(Regex("$wordToOmit[_-]?"), "")
        }

        naming.modulePackageDelimiter?.let { delimiter ->
            modulePackage = modulePackage.replace(Regex("[-_]"), delimiter)
        }

        return naming.appPackage + "." + modulePackage
    }
}
