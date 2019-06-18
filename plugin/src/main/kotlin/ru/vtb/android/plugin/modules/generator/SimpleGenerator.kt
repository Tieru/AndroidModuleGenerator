package ru.vtb.android.plugin.modules.generator

import ru.vtb.android.plugin.modules.entity.ModuleConfig

interface SimpleGenerator {

    fun generate(config: ModuleConfig)

}
