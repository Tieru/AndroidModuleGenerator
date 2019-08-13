package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.entity.ModuleNameModifier
import ru.vtb.android.plugin.modules.entity.buildClassNameAround
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.retrievePackageAndClassName
import java.io.File


data class ApiFeatureStarterConfig(
    val featureStarterDir: File,
    val classPackage: String,
    val featureName: String,
    val featureNameModifiers: List<ModuleNameModifier>,
    val baseScreenClass: String
)

class ApiFeatureStarterGenerator(private val config: ApiFeatureStarterConfig): SimpleGenerator {

    override fun generate() {
        val className = config.featureNameModifiers.buildClassNameAround(config.featureName, "Starter")
        val screenClass = config.baseScreenClass.retrievePackageAndClassName()
        val file = FileSpec.builder(config.classPackage, className)
            .addImport(screenClass.first, screenClass.second)
            .addType(TypeSpec.interfaceBuilder(className)
                .addFunction(FunSpec.builder("getScreen")
                    .addModifiers(KModifier.ABSTRACT)
                    .returns(ClassName(screenClass.first, screenClass.second))
                    .build())
                .build()
            ).build()

        file.writeTo(config.featureStarterDir)
    }
}
