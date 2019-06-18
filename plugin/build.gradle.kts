plugins {
    `kotlin-dsl`
    groovy
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
}
