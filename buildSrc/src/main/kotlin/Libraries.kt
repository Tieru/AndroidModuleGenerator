private object Versions {
	const val jackson = "2.9.9"
	const val kotlinPoet = "1.3.0"
}

object Libraries {
	val jacksonCore = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
	val jacksonKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
	val jacksonYaml = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.jackson}"
	val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
}
