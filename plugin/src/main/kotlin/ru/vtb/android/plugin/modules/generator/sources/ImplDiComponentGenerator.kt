package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.entity.ModuleNameModifier
import ru.vtb.android.plugin.modules.entity.buildClassNameAround
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.retrievePackageAndClassName
import java.io.File


data class ImplDiFeatureComponentConfig(
    val moduleSourcesDir: File,
    val modulePackage: String,
    val featureName: String,
    val featureNameModifiers: List<ModuleNameModifier>,
    val apiDiPackage: String,
    val featureScopeClass: String?
)


class ImplDiComponentGenerator(private val config: ImplDiFeatureComponentConfig) : SimpleGenerator {

    private companion object {
        const val diDir = "di"
    }

    private val diPackage = config.modulePackage + "." + diDir
    private val featureScopeClass = config.featureScopeClass?.retrievePackageAndClassName()

    private val featureComponentClassName =
        config.featureNameModifiers.buildClassNameAround(config.featureName, "Component")
    private val featureModuleClassName =
        config.featureNameModifiers.buildClassNameAround(config.featureName, "Module")
    private val featureDependenciesClassName =
        config.featureNameModifiers.buildClassNameAround(config.featureName, "Dependencies")
    private val apiComponentClassName =
        config.featureNameModifiers.buildClassNameAround(config.featureName, "Api")

    override fun generate() {
        makeFeatureComponent()
        writeDependenciesInterface()
    }

    private fun makeFeatureComponent() {
        val file = FileSpec.builder(diPackage, featureComponentClassName)
            .addImport("dagger", "Component")

        if (diPackage != config.apiDiPackage) {
            file.addImport(config.apiDiPackage, apiComponentClassName, featureDependenciesClassName)
        }

        val classBuilder = TypeSpec.classBuilder(featureComponentClassName)
            .addSuperinterface(ClassName(config.apiDiPackage, apiComponentClassName))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger", "Component"))
                    .addMember("modules = [$featureModuleClassName::class]")
                    .addMember("dependencies = [$featureDependenciesClassName::class]")
                    .build()
            )
            .addModifiers(KModifier.ABSTRACT)

        featureScopeClass?.let { scope ->
            file.addImport(scope.first, scope.second)
            classBuilder.addAnnotation(ClassName(scope.first, scope.second))
        }

        classBuilder.addType(makeInnerDependenciesComponent())
        classBuilder.addFunction(makeGetScreenComponentFunction())
        classBuilder.addType(makeCompanionObject())
        file.addType(classBuilder.build())
        file.build().writeTo(config.moduleSourcesDir)
    }

    private fun makeInnerDependenciesComponent(): TypeSpec {
        val interfaceName =
            config.featureNameModifiers.buildClassNameAround(config.featureName, "FeatureDependenciesComponent")

        val innerDependenciesInterface = TypeSpec.interfaceBuilder(interfaceName)
            .addSuperinterface(ClassName(config.apiDiPackage, featureDependenciesClassName))
            .addModifiers(KModifier.INTERNAL)
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger", "Component"))
                    .addMember("dependencies = []")
                    .build()
            )

        featureScopeClass?.let { scope ->
            innerDependenciesInterface.addAnnotation(ClassName(scope.first, scope.second))
        }

        return innerDependenciesInterface.build()
    }

    private fun makeGetScreenComponentFunction(): FunSpec {
        val screenComponentClassName = config.featureName + "ScreenComponent"
        val functionName = screenComponentClassName.decapitalize()
        return FunSpec.builder(functionName)
            .addModifiers(KModifier.ABSTRACT)
            .returns(ClassName(diPackage, screenComponentClassName))
            .build()
    }

    private fun makeCompanionObject(): TypeSpec {
        val componentInstanceVariable = featureComponentClassName.decapitalize()

        val companion = TypeSpec.companionObjectBuilder()
            .addProperty(PropertySpec.builder(componentInstanceVariable,
                ClassName(diPackage, featureComponentClassName).copy(nullable = true))
                .mutable()
                .initializer("null")
                .addModifiers(KModifier.PRIVATE)
                .addAnnotation(ClassName("kotlin.jvm", "Volatile"))
                .build()
            )
            .addFunction(
                FunSpec.builder("get")
                    .returns(ClassName(diPackage, featureComponentClassName))
                    .addStatement("return $componentInstanceVariable " +
                        "?: throw IllegalStateException(\"Component '$featureComponentClassName' is not initialized\")")
                    .build()
            )
            .addFunction(
                FunSpec.builder("init")
                    .addAnnotation(ClassName("kotlin.jvm", "Synchronized"))
                    .addParameter(
                        ParameterSpec.builder("dependencies",
                            ClassName(diPackage, featureDependenciesClassName)).build()
                    )
                    .returns(ClassName(diPackage, apiComponentClassName))
                    .addStatement(
                        """
                            val $componentInstanceVariable = Dagger${featureComponentClassName}.builder()
                                .${featureDependenciesClassName.decapitalize()}(dependencies)
                                .build()
                            this.$componentInstanceVariable = $componentInstanceVariable
                            return $componentInstanceVariable
                        """.trimIndent()
                    )
                    .build()
            )

        return companion.build()
    }

    private fun writeDependenciesInterface() {
        val file = FileSpec.builder(diPackage, featureDependenciesClassName)

        file.addType(
            TypeSpec.interfaceBuilder(featureDependenciesClassName).build()
        )

        file.build().writeTo(config.moduleSourcesDir)
    }

}
