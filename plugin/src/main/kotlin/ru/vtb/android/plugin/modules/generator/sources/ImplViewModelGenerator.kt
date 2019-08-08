package ru.vtb.android.plugin.modules.generator.sources

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import java.io.File

data class ViewModelConfig(
    val moduleSrcDir: File,
    val modulePackage: String,
    val featureName: String
)

class ImplViewModelGenerator(private val config: ViewModelConfig) : SimpleGenerator {

    private companion object {
        const val presentationDir = "presentation"
    }

    override fun generate() {
        val viewModelPackage = config.modulePackage + "." + presentationDir
        val viewModelName = config.featureName + "ViewModel"

        val file = FileSpec.builder(viewModelPackage, viewModelName)
            .addType(
                TypeSpec.classBuilder(viewModelName)
                    .build()
            ).build()

        file.writeTo(config.moduleSrcDir)
    }
}
