package ru.vtb.android.plugin.modules.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.vtb.android.plugin.modules.config.*
import java.util.*

open class ModuleGeneratorTask : DefaultTask() {

    @Suppress("unused")
    @TaskAction
    fun generate() {
        val moduleName = Scanner(System.`in`).nextLine()
        val projectConfig = ProjectConfigReader().readProjectConfig(project)

        val modules = listOf(
            ApiModuleConfigGenerator(project, moduleName, projectConfig.naming, projectConfig.apiModule),
            ImplModuleConfigGenerator(
                project, moduleName, projectConfig.naming, projectConfig.implModule, projectConfig.apiModule
            )
        )

        modules
            .forEach { type ->
                type.makeModuleType().generators.forEach { generator ->
                    generator.generate()
                }
            }

        println("Success")
    }
}
