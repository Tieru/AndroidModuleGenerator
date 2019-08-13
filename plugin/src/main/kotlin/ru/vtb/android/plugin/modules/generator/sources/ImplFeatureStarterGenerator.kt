package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.entity.ModuleNameModifier
import ru.vtb.android.plugin.modules.entity.buildClassNameAround
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.retrievePackageAndClassName
import java.io.File
import javax.inject.Inject


data class ImplFeatureStarterConfig(
    val featureStarterDir: File,
    val classPackage: String,
    val featureName: String,
    val featureNameModifiers: List<ModuleNameModifier>,
    val baseScreenClass: String,
    val apiStarterPackage: String,
    val addInjectConstructor: Boolean
)

class ImplFeatureStarterGenerator(private val config: ImplFeatureStarterConfig) : SimpleGenerator {

    override fun generate() {
        val className = config.featureNameModifiers.buildClassNameAround(config.featureName, "StarterImpl")
        val interfaceName = config.featureNameModifiers.buildClassNameAround(config.featureName, "Starter")
        val screenClass = config.baseScreenClass.retrievePackageAndClassName()

        val file = FileSpec.builder(config.classPackage, className)
            .addImport(screenClass.first, screenClass.second)

        if (config.addInjectConstructor) {
            file.addImport("javax.inject", "Inject")
        }

        if (config.apiStarterPackage != config.classPackage) {
            file.addImport(config.apiStarterPackage, interfaceName)
        }

        file.addType(
            TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName(config.apiStarterPackage, interfaceName))
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addAnnotation(Inject::class)
                    .build()
                )
                .addFunction(
                    FunSpec.builder("getScreen")
                        .addModifiers(KModifier.OVERRIDE)
                        .addStatement("throw NotImplementedError(\"$className.getScreen is not implemented\")")
                        .returns(ClassName(screenClass.first, screenClass.second))
                        .build()
                )
                .build()
        )

        file.build().writeTo(config.featureStarterDir)
    }
}
