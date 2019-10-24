package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.entity.ModuleNameModifier
import ru.vtb.android.plugin.modules.entity.buildClassNameAround
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.retrievePackageAndClassName
import java.io.File


data class ImplDiConfig(
    val moduleSourcesDir: File,
    val modulePackage: String,
    val featureName: String,
    val featureNameModifiers: List<ModuleNameModifier>,
    val apiDiPackage: String,
    val addViewModel: Boolean,
    val featureStarterPackage: String?,
    val featureScopeClass: String?,
    val screenScopeClass: String?
)

class ImplDiGenerator(private val config: ImplDiConfig) : SimpleGenerator {

    private companion object {
        const val diDir = "di"
    }

    private val diPackage = config.modulePackage + "." + diDir
    private val featureModuleClassName =
        config.featureNameModifiers.buildClassNameAround(config.featureName, "Module")
    private val screenModuleClassName = config.featureName + "ScreenModule"

    private val featureScopeClass = config.featureScopeClass?.retrievePackageAndClassName()
    private val screenScopeClass = config.screenScopeClass?.retrievePackageAndClassName()

    override fun generate() {
        makeFeatureModule()
        makeScreenComponent()
    }

    private fun makeFeatureModule() {
        val file = FileSpec.builder(diPackage, featureModuleClassName)
            .addImport("dagger", "Module")

        val viewModelPackage = config.modulePackage + ".presentation"
        val viewModelClass = config.featureName + "ViewModel"

        val featureStarterPackage = config.featureStarterPackage
        val featureStarterClass = config.featureNameModifiers.buildClassNameAround(config.featureName, "Starter")

        if (config.addViewModel || featureStarterPackage != null) {
            file.addImport("dagger", "Provides")
        }

        if (featureStarterPackage != null) {
            file.addImport(featureStarterPackage, featureStarterClass, featureStarterClass + "Impl")
        }

        if (config.addViewModel) {
            file.addImport(viewModelPackage, viewModelClass)
        }

        featureScopeClass?.let { file.addImport(it.first, it.second) }

        val featureModule = TypeSpec.classBuilder(featureModuleClassName)
            .addAnnotation(ClassName("dagger", "Module"))

        if (featureStarterPackage != null) {
            val featureStarterFunction = FunSpec.builder("provideFeatureStarter")
                .addAnnotation(ClassName("dagger", "Provides"))
                .returns(ClassName(featureStarterPackage, featureStarterClass))
                .addStatement("return ${featureStarterClass}Impl()")

            featureScopeClass?.let { scope ->
                featureStarterFunction.addAnnotation(ClassName(scope.first, scope.second))
            }

            featureModule.addFunction(featureStarterFunction.build())
        }

        val screenModule = TypeSpec.classBuilder(screenModuleClassName)
            .addAnnotation(ClassName("dagger", "Module"))

        if (config.addViewModel) {
            val viewModelFunction = FunSpec.builder("provideViewModel")
                .addAnnotation(ClassName("dagger", "Provides"))
                .returns(ClassName(viewModelPackage, viewModelClass))
                .addStatement("return $viewModelClass()")

            screenScopeClass?.let { scope ->
                viewModelFunction.addAnnotation(ClassName(scope.first, scope.second))
            }

            screenModule.addFunction(viewModelFunction.build())
        }

        file.addType(featureModule.build())
            .addType(screenModule.build())


        file.build().writeTo(config.moduleSourcesDir)
    }

    private fun makeScreenComponent() {
        val screenComponentClassName = config.featureName + "ScreenComponent"
        val file = FileSpec.builder(diPackage, screenComponentClassName)
            .addImport("dagger", "Subcomponent")

        val componentBuilder = TypeSpec.interfaceBuilder(screenComponentClassName)
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger", "Subcomponent"))
                    .addMember("modules = [$screenModuleClassName::class]")
                    .build()
            )

        screenScopeClass?.let { scope ->
            file.addImport(scope.first, scope.second)
            componentBuilder.addAnnotation(ClassName(scope.first, scope.second))
        }

        file.addType(componentBuilder.build())
        file.build().writeTo(config.moduleSourcesDir)
    }
}
