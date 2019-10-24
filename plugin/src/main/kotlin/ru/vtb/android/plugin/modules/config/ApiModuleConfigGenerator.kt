package ru.vtb.android.plugin.modules.config

import org.gradle.api.Project
import ru.vtb.android.plugin.modules.entity.ApiModuleOptions
import ru.vtb.android.plugin.modules.entity.ModuleType
import ru.vtb.android.plugin.modules.entity.ProjectNamingOptions
import ru.vtb.android.plugin.modules.generator.*
import ru.vtb.android.plugin.modules.generator.sources.*
import java.io.File

class ApiModuleConfigGenerator(
    private val project: Project,
    private val moduleName: String,
    private val naming: ProjectNamingOptions,
    private val options: ApiModuleOptions
) : BasicModuleConfigGenerator() {

    override fun makeModuleType(): ModuleType {
        val featureModuleName =
            makeModuleName(moduleName, options.postfix, naming.moduleNameDelimiter)
        val sourcesConfig = makeSourcesConfig(project, moduleName, featureModuleName,
            options.addPostfixToPackageName, naming)

        val generators = mutableListOf<SimpleGenerator>()

        val moduleStructureConfig = ModuleStructureConfig(sourcesConfig.moduleClassesDir)
        generators.add(BasicModuleStructureGenerator(moduleStructureConfig))

        if (options.addManifest) {
            val manifestConfig = ManifestConfig(sourcesConfig.moduleSrcMainDir.path,
                sourcesConfig.modulePackage,
                ".api")
            generators.add(ManifestGenerator(manifestConfig))
        }

        if (options.presentation.addFeatureStarter) {
            val presentation = options.presentation
            val starterPackage = sourcesConfig.modulePackage + "." + presentation.featureStarterPackage
            val starterConfig = ApiFeatureStarterConfig(
                sourcesConfig.moduleClassesDir, starterPackage,
                sourcesConfig.cleanFeatureNameCamelCase, sourcesConfig.featureNameModifiers,
                presentation.featureStarterBaseScreen
            )
            generators.add(ApiFeatureStarterGenerator(starterConfig))
        }

        if (options.di.enabled) {
            val config = ApiDiConfig(
                sourcesConfig.moduleClassesDir,
                sourcesConfig.modulePackage + ".di",
                sourcesConfig.cleanFeatureNameCamelCase, sourcesConfig.featureNameModifiers,
                options.di.addFeatureStarter,
                sourcesConfig.modulePackage + "." + options.presentation.featureStarterPackage
            )
            generators.add(ApiDiGenerator(config))
        }

        options.buildGradleTemplate?.let { templatePath ->
            val config = BuildFileConfig(
                project.rootProject.projectDir,
                templatePath,
                sourcesConfig.moduleDir
            )
            generators.add(BuildFileGenerator(config))
        }

        val settingsConfig = SettingsConfig(project, sourcesConfig.moduleName)
        generators.add(SettingsGenerator(settingsConfig))

        return ModuleType(generators)
    }
}
