import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*

/**
 * Configures the current project as a Kotlin project by adding the Kotlin `stdlib` as a dependency.
 */
fun Project.kotlinProject() {
	dependencies {
		"compile"(kotlin("stdlib"))
	}
}

fun Project.setupAppBundle(mainClass: String? = null) {
	val configuration = configurations.get("runtimeClasspath")
	val bundleAppTask = task("fatJar", type = Jar::class) {
		baseName = project.name
		manifest {
			mainClass?.let {
				attributes["Main-Class"] = mainClass
			}
		}
		from(configuration.map { if (it.isDirectory) it else zipTree(it) })

		with(tasks["jar"] as CopySpec)
	}

	tasks {
		"build" {
			dependsOn(bundleAppTask)
		}
	}
}
