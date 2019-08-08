package ru.vtb.android.plugin.modules.utils

import java.security.InvalidParameterException

fun String.toCamelCase(): String {
    val words = split("_", "-", " ")
    val capitalized = words.map { it.capitalize() }
    return capitalized.joinToString(separator = "")
}

fun String.retrievePackageAndClassName(): Pair<String, String> {
    val parts = split(".")
    if (parts.size < 2) {
        throw InvalidParameterException("'fullyClassifiedClassName' should be package + class name. E.g. 'ru.blah.blah.Screen'. Value $this received instead")
    }
    return parts.dropLast(1).joinToString(separator = ".") to parts.last()
}
