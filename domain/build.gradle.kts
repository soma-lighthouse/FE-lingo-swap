plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(libs.kotlin.coroutines)
    implementation(libs.java.inject)

    dependencies {
        api(project(mapOf("path" to ":lighthousei18n")))
    }
}