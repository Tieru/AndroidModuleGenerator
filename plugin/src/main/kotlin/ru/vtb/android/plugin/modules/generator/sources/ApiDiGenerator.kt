package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.entity.ModuleNameModifier
import ru.vtb.android.plugin.modules.entity.buildClassNameAround
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import java.io.File


data class ApiDiConfig(
    val diDir: File,
    val classPackage: String,
    val featureName: String,
    val featureNameModifiers: List<ModuleNameModifier>,
    val addFeatureStarter: Boolean,
    val featureStarterClassPackage: String
)

class ApiDiGenerator(private val config: ApiDiConfig) : SimpleGenerator {

    override fun generate() {
        val fileName = config.featureNameModifiers.buildClassNameAround(config.featureName)
        val featureApiClassName = config.featureNameModifiers.buildClassNameAround(config.featureName, "Api")

        val featureStarterClassName = config.featureNameModifiers.buildClassNameAround(config.featureName, "Starter")

        val featureApiBuilder = TypeSpec.interfaceBuilder(featureApiClassName)
        if (config.addFeatureStarter) {
            featureApiBuilder.addFunction(
                FunSpec.builder("getFeatureStarter")
                    .addModifiers(KModifier.ABSTRACT)
                    .returns(ClassName(config.featureStarterClassPackage, featureStarterClassName))
                    .build()
            )
        }

        val file = FileSpec.builder(config.classPackage, fileName)
        if (config.addFeatureStarter) {
            file.addImport(config.featureStarterClassPackage, featureStarterClassName)
        }

        file.addType(featureApiBuilder.build())
        file.build().writeTo(config.diDir)
    }

}
