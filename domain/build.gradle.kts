plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":entity"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("androidx.paging:paging-common:3.2.0")
    implementation("javax.inject:javax.inject:1")
}