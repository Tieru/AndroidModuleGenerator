package ru.vtb.android.plugin.modules.utils

fun String.toCamelCase(): String {
    val words = split("_", "-", " ")
    val capitalized = words.map { it.capitalize() }
    return capitalized.joinToString(separator = "")
}
