package ru.vtb.android.plugin.modules.task

import ru.vtb.android.plugin.modules.generator.ConfigGenerator
import ru.vtb.android.plugin.modules.generator.ModuleGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.vtb.android.plugin.modules.entity.ModuleType
import ru.vtb.android.plugin.modules.generator.sources.ApiGenerator
import ru.vtb.android.plugin.modules.generator.sources.ImplDIGenerator
import ru.vtb.android.plugin.modules.generator.sources.ImplPresentationGenerator
import ru.vtb.android.plugin.modules.generator.sources.ImplUiGenerator
import java.util.*

open class ModuleGeneratorTask : DefaultTask() {

    private val configGenerator = ConfigGenerator()
    private val moduleGenerator = ModuleGenerator()

    @Suppress("unused")
    @TaskAction
    fun generate() {
        val moduleName = Scanner(System.`in`).nextLine()

        val modules = listOf(
            makeApiModuleType(),
            makeImplModuleType()
        )

        modules
            .forEach { type ->
                val fullModuleName = type.prefix + moduleName + type.postfix
                val config = configGenerator.generate(project, fullModuleName)
                moduleGenerator.generateModule(config)

                type.additionalGenerators.forEach { generator ->
                    generator.generate(config)
                }
            }

        println("Success")
    }

    private fun makeApiModuleType(): ModuleType {
        val generators = listOf(
            ApiGenerator()
        )
        return ModuleType(postfix = "-api", additionalGenerators = generators)
    }

    private fun makeImplModuleType(): ModuleType {
        val generators = listOf(
            ImplDIGenerator(),
            ImplPresentationGenerator(),
            ImplUiGenerator()
        )

        return ModuleType(postfix = "-impl", additionalGenerators = generators)
    }
}
