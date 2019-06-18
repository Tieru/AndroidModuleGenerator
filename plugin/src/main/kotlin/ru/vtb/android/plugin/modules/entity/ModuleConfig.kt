package ru.vtb.android.plugin.modules.entity

import org.gradle.api.Project

data class ModuleConfig (
    val name: String,
    val underscoreName: String,

    val absolutePath: String,
    val relativePath: String,
    val project: Project,
    val buildGradlePath: String,

    val templatesPath: String,
    val sourceConfig: SourceConfiguration
)
