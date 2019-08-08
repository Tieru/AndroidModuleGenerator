package ru.vtb.android.plugin.modules.entity

sealed class ModuleNameModifier(val name: String)

object Feature: ModuleNameModifier("Feature")

fun List<ModuleNameModifier>.buildClassNameAround(featureName: String, className: String = ""): String {
    var name = featureName
    for (modifier in this) {
        when (modifier) {
            is Feature -> name += "Feature"
        }
    }
    return name + className
}