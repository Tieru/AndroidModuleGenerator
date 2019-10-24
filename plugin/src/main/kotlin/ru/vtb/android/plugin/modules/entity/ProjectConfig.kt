package ru.vtb.android.plugin.modules.entity


data class ProjectConfig(
    val naming: ProjectNamingOptions,
    val apiModule: ApiModuleOptions,
    val implModule: ImplModuleOptions
)

data class ProjectNamingOptions(
    val appPackage: String,
    val omitWordsInPackage: List<String>,
    val moduleNameDelimiter: String,
    val modulePackageDelimiter: String?
)

data class ImplModuleOptions(
    val postfix: String? = "impl",
    val addPostfixToPackageName: Boolean = true,
    val addManifest: Boolean,
    val buildGradleTemplate: String? = "tools/module-templates/build-template.gradle",
    val di: ImplModuleDiOptions,
    val presentation: ImplModulePresentationOptions,
    val ui: ImplModuleUiOptions
)

data class ImplModuleDiOptions(
    val enabled: Boolean,
    val featureScopeAnnotationClass: String? = null,
    val screenScopeAnnotationClass: String? = null
)

data class ImplModulePresentationOptions(
    val enabled: Boolean = true,
    val addFeatureStarter: Boolean = true,
    val addFeatureStarterInjectConstructor: Boolean = true,
    val addViewModel: Boolean = true
)

data class ImplModuleUiOptions(
    val enabled: Boolean = true,
    val baseFragmentClass: String?,
    val injectViewModel: Boolean
)

data class ApiModuleOptions(
    val postfix: String? = "api",
    val addPostfixToPackageName: Boolean = true,
    val addManifest: Boolean = true,
    val buildGradleTemplate: String? = "tools/module-templates/build-template.gradle",
    val di: ApiModuleDiOptions = ApiModuleDiOptions(),
    val presentation: ApiModulePresentationOptions = ApiModulePresentationOptions()
)

data class ApiModuleDiOptions(
    val enabled: Boolean = true,
    val addFeatureStarter: Boolean = true
)

data class ApiModulePresentationOptions(
    val addFeatureStarter: Boolean = true,
    val featureStarterPackage: String = "presentation",
    val featureStarterBaseScreen: String = "ru.terrakok.cicerone.android.support.SupportAppScreen"
)
