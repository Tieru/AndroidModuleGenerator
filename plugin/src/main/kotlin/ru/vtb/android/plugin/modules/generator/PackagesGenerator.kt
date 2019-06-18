package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.entity.SourceConfiguration
import java.io.File

class PackagesGenerator {

    fun generate(config: SourceConfiguration) {
        val classes = File(config.classPath)
        classes.mkdirs()
    }
}
