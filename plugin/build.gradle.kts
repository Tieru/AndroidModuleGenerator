plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.1"
}

repositories {
    jcenter()
}

dependencies {
    implementation(Libraries.jacksonCore)
    implementation(Libraries.jacksonKotlin)
    implementation(Libraries.jacksonYaml)
    implementation(Libraries.kotlinPoet)
}

val bundleAppTask = task("fatJar", type = Jar::class) {
    from(configurations["runtimeClasspath"]
        .filter {
            !it.name.contains("gradle") && !it.name.contains("groovy")// && !it.name.contains("kotlin")
        }
        .map { if (it.isDirectory) it else zipTree(it) })

    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(bundleAppTask)
    }
}
