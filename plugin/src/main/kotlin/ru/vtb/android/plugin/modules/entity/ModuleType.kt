package ru.vtb.android.plugin.modules.entity

import ru.vtb.android.plugin.modules.generator.SimpleGenerator

data class ModuleType(
    val prefix: String = "",
    val postfix: String = "",
    val additionalGenerators: List<SimpleGenerator>
)
