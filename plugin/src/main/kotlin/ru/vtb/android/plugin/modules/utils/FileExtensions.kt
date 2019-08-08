package ru.vtb.android.plugin.modules.utils

import java.io.File

fun File.makeFileWithContent(content: String) {
    if (exists()) {
        throw IllegalStateException("File with path $path already exists")
    }

    val dir = File(parent)
    if (!dir.exists()) {
        dir.mkdirs()
    }

    println("Making a new file: $path")

    createNewFile()
    writeText(content)
}