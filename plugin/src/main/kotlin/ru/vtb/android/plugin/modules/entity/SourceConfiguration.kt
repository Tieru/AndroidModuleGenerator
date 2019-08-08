package ru.vtb.android.plugin.modules.entity

import java.io.File

data class SourceConfiguration(
    val moduleName: String,
    val modulePackage: String,
    val moduleDir: File,
    val moduleSrcMainDir: File,
    val moduleClassesDir: File,

    val cleanFeatureNameCamelCase: String,
    val fullFeatureName: String,
    val featureNameModifiers: List<ModuleNameModifier>
)
