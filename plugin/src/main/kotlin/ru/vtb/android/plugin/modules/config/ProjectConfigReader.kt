package ru.vtb.android.plugin.modules.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.Project
import ru.vtb.android.plugin.modules.entity.ProjectConfig
import java.io.File

class ProjectConfigReader {

    fun readProjectConfig(project: Project): ProjectConfig {
        val configPath = File("${project.rootProject.projectDir}/tools/module-templates/project-config.yaml")

        val factory = YAMLFactory().disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
        val mapper = ObjectMapper(factory).registerModule(KotlinModule())
        return mapper.readValue(configPath)
    }
}
