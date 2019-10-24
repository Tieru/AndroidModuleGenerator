package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.utils.makeFileWithContent
import java.io.File

class GitIgnoreGenerator(private val moduleDir: String): SimpleGenerator {

    override fun generate() {
        val content = "/build"

        val file = File("$moduleDir/.gitignore")
        file.makeFileWithContent(content)
    }
}