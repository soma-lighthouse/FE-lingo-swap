import org.jetbrains.kotlin.konan.properties.Properties

val lighthouseFile = rootProject.file("lighthouse.properties")
val properties = Properties()
properties.load(lighthouseFile.inputStream())

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.auth"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    defaultConfig {
        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            properties.getProperty("GOOGLE_CLIENT_ID")
        )
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":common-ui"))
    implementation(project(":navigation"))

    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.bundles.android.basic.ui)
    implementation(libs.kotlin.coroutines)
    implementation(libs.hilt)
    implementation("androidx.core:core-splashscreen:1.0.0-beta02")
    implementation(libs.google.login)
    implementation("com.google.guava:guava:31.0.1-android")

    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.image)
    implementation(libs.bundles.camerax)
}