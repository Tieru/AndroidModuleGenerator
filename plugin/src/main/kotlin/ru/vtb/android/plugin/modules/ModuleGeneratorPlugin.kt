@file:Suppress("unused")

package ru.vtb.android.plugin.modules

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import ru.vtb.android.plugin.modules.task.ModuleGeneratorTask

class ModuleGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.tasks.register<ModuleGeneratorTask>("generate") {
            group = "generation"
        }
    }
}
