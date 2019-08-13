package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.retrievePackageAndClassName
import java.io.File

data class UiConfig(
    val moduleDir: File,
    val modulePackage: String,
    val featureName: String,
    val baseFragmentClass: String,
    val injectViewModel: Boolean
)

class ImplUiGenerator(private val config: UiConfig) : SimpleGenerator {

    override fun generate() {
        val uiPackage = config.modulePackage + ".ui"
        val fragmentClass = config.featureName + "Fragment"
        val fragmentSuperClass = config.baseFragmentClass.retrievePackageAndClassName()

        val file = FileSpec.builder(uiPackage, fragmentClass)
            .addImport(fragmentSuperClass.first, fragmentSuperClass.second)

        val fragment = TypeSpec.classBuilder(fragmentClass)
            .superclass(ClassName(fragmentSuperClass.first, fragmentSuperClass.second))

        if (config.injectViewModel) {
            val viewModelPackage = config.modulePackage + ".presentation"
            val viewModelClassName = config.featureName + "ViewModel"

            file.addImport(viewModelPackage, viewModelClassName)
                .addImport("javax.inject", "Inject")
            fragment.addProperty(
                PropertySpec.builder("viewModel", ClassName(viewModelPackage, viewModelClassName))
                    .mutable()
                    .addModifiers(KModifier.LATEINIT)
                    .addAnnotation(ClassName("javax.inject", "Inject"))
                    .build()
            )
        }

        file.addType(fragment.build())

        file.build().writeTo(config.moduleDir)
    }
}
