package ru.vtb.android.plugin.modules.generator.sources

import ru.vtb.android.plugin.modules.generator.SimpleGenerator
import java.io.File
import java.lang.IllegalStateException


data class ModuleStructureConfig(
    val moduleClassesDir: File
)

class BasicModuleStructureGenerator(private val config: ModuleStructureConfig):
    SimpleGenerator {

    override fun generate() {
        config.moduleClassesDir.mkdirs()
        if (!config.moduleClassesDir.exists()) {
            throw IllegalStateException("Module classes dir was not created")
        }
    }
}