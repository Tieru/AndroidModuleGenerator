package ru.vtb.android.plugin.modules.config

import org.gradle.api.Project
import ru.vtb.android.plugin.modules.entity.ApiModuleOptions
import ru.vtb.android.plugin.modules.entity.ImplModuleOptions
import ru.vtb.android.plugin.modules.entity.ModuleType
import ru.vtb.android.plugin.modules.entity.ProjectNamingOptions
import ru.vtb.android.plugin.modules.generator.*
import ru.vtb.android.plugin.modules.generator.sources.*
import java.lang.IllegalArgumentException

class ImplModuleConfigGenerator(
    private val project: Project,
    private val moduleName: String,
    private val naming: ProjectNamingOptions,
    private val options: ImplModuleOptions,
    private val apiOptions: ApiModuleOptions
) : BasicModuleConfigGenerator() {

    override fun makeModuleType(): ModuleType {
        val featureModuleName =
            makeModuleName(moduleName, options.postfix, naming.moduleNameDelimiter)
        val sourcesConfig = makeSourcesConfig(project, moduleName, featureModuleName,
            options.addPostfixToPackageName, naming)

        val apiFeatureName = makeModuleName(moduleName, options.postfix, naming.moduleNameDelimiter)
        val apiSourcesConfig = makeSourcesConfig(project, moduleName, apiFeatureName,
            apiOptions.addPostfixToPackageName, naming)

        val generators = mutableListOf<SimpleGenerator>()

        val moduleStructureConfig = ModuleStructureConfig(sourcesConfig.moduleClassesDir)
        generators.add(BasicModuleStructureGenerator(moduleStructureConfig))

        if (options.addManifest) {
            val manifestConfig = ManifestConfig(sourcesConfig.moduleSrcMainDir.path, sourcesConfig.modulePackage)
            generators.add(ManifestGenerator(manifestConfig))
        }

        if (options.presentation.addFeatureStarter) {
            val apiPresentation = apiOptions.presentation
            val starterPackage = sourcesConfig.modulePackage + "." + apiPresentation.featureStarterPackage
            val starterConfig = ImplFeatureStarterConfig(
                sourcesConfig.moduleClassesDir, starterPackage,
                sourcesConfig.cleanFeatureNameCamelCase, sourcesConfig.featureNameModifiers,
                apiPresentation.featureStarterBaseScreen,
                apiSourcesConfig.modulePackage + "." + apiPresentation.featureStarterPackage,
                options.presentation.addFeatureStarterInjectConstructor
            )
            generators.add(ImplFeatureStarterGenerator(starterConfig))
        }

        if (options.presentation.addViewModel) {
            val viewModelConfig = ViewModelConfig(
                sourcesConfig.moduleClassesDir, sourcesConfig.modulePackage,
                sourcesConfig.cleanFeatureNameCamelCase
            )
            generators.add(ImplViewModelGenerator(viewModelConfig))
        }

        if (options.di.enabled) {
            val featureStarter = if (apiOptions.presentation.addFeatureStarter)
                sourcesConfig.modulePackage + "." + apiOptions.presentation.featureStarterPackage
            else
                null
            val config = ImplDiConfig(
                sourcesConfig.moduleClassesDir,
                sourcesConfig.modulePackage,
                sourcesConfig.cleanFeatureNameCamelCase, sourcesConfig.featureNameModifiers,
                apiSourcesConfig.modulePackage + ".di",
                options.presentation.addViewModel,
                featureStarter,
                options.di.featureScopeAnnotationClass,
                options.di.screenScopeAnnotationClass
            )
            generators.add(ImplDiGenerator(config))

            val implDiComponentGeneratorConfig = ImplDiFeatureComponentConfig(
                sourcesConfig.moduleClassesDir,
                sourcesConfig.modulePackage,
                sourcesConfig.cleanFeatureNameCamelCase, sourcesConfig.featureNameModifiers,
                apiSourcesConfig.modulePackage + ".di",
                options.di.featureScopeAnnotationClass
            )
            generators.add(ImplDiComponentGenerator(implDiComponentGeneratorConfig))
        }

        options.buildGradleTemplate?.let { templatePath ->
            val config = BuildFileConfig(
                project.rootProject.projectDir,
                templatePath,
                sourcesConfig.moduleDir
            )
            generators.add(BuildFileGenerator(config))
        }

        if (options.ui.enabled) {
            val baseFragment = options.ui.baseFragmentClass ?: throw IllegalArgumentException("baseFragmentClass should be set")
            val uiConfig = UiConfig(
                sourcesConfig.moduleClassesDir,
                sourcesConfig.modulePackage,
                sourcesConfig.cleanFeatureNameCamelCase,
                baseFragment,
                options.ui.injectViewModel
            )
            generators.add(ImplUiGenerator(uiConfig))
        }

        val settingsConfig = SettingsConfig(project, sourcesConfig.moduleName)
        generators.add(SettingsGenerator(settingsConfig))

        return ModuleType(generators)
    }
}
