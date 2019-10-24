package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.*
import ru.vtb.android.plugin.modules.entity.ModuleNameModifier
import ru.vtb.android.plugin.modules.entity.buildClassNameAround
import ru.vtb.android.plugin.modules.generator.ResourcesGenerator
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import ru.vtb.android.plugin.modules.utils.retrievePackageAndClassName
import java.io.File

data class UiConfig(
    val moduleDir: File,
    val modulePackage: String,
    val moduleName: String,
    val featureName: String,
    val featureModifiers: List<ModuleNameModifier>,
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

            val featureComponentClassName =
                config.featureModifiers.buildClassNameAround(config.featureName, "Component")
            val screenComponentClassName = config.featureName + "ScreenComponent"
            val diComponentPackage = config.modulePackage + ".di"

            file.addImport(viewModelPackage, viewModelClassName)
                .addImport("javax.inject", "Inject")
                .addImport(diComponentPackage, featureComponentClassName)
                .addImport(config.modulePackage, "R")

            fragment.addProperty(
                PropertySpec.builder("viewModel", ClassName(viewModelPackage, viewModelClassName))
                    .mutable()
                    .addModifiers(KModifier.LATEINIT)
                    .addAnnotation(ClassName("javax.inject", "Inject"))
                    .build()
            )
                .addFunction(
                    FunSpec.builder("onCreate")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter(
                            ParameterSpec.builder(
                                "savedInstanceState",
                                ClassName("android.os", "Bundle").copy(nullable = true)
                            ).build()
                        )
                        .addStatement("super.onCreate(savedInstanceState)\n")
                        .addStatement("$featureComponentClassName.get().${screenComponentClassName.decapitalize()}().inject(this)")
                        .build()
                )
                .addFunction(
                    makeCreateViewFunction()
                )
                .addFunction(
                    FunSpec.builder("onViewCreated")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter(
                            ParameterSpec.builder(
                                "view",
                                ClassName("android.view", "View")
                            ).build()
                        )
                        .addParameter(
                            ParameterSpec.builder(
                                "savedInstanceState",
                                ClassName("android.os", "Bundle").copy(nullable = true)
                            ).build()
                        )
                        .addStatement("super.onViewCreated(view, savedInstanceState)")
                        .build()
                )
        }

        file.addType(fragment.build())

        file.build().writeTo(config.moduleDir)
    }

    private fun makeCreateViewFunction(): FunSpec {
        return FunSpec.builder("onCreateView")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(
                ParameterSpec.builder(
                    "inflater",
                    ClassName("android.view", "LayoutInflater")
                ).build()
            )
            .addParameter(
                ParameterSpec.builder(
                    "container",
                    ClassName("android.view", "ViewGroup").copy(nullable = true)
                ).build()
            )
            .addParameter(
                ParameterSpec.builder(
                    "savedInstanceState",
                    ClassName("android.os", "Bundle").copy(nullable = true)
                ).build()
            )
            .returns(ClassName("android.view", "View").copy(nullable = true))
            .addStatement(
                "return inflater.inflate(" +
                        "R.layout.${ResourcesGenerator.featureNameToFragmentFileName(config.moduleName)}, container, false)")
            .build()
    }
}
