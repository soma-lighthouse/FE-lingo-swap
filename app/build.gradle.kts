import org.jetbrains.kotlin.konan.properties.Properties

val lighthouseFile = rootProject.file("lighthouse.properties")
val properties = Properties()
properties.load(lighthouseFile.inputStream())

plugins {
    kotlin("android")
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.lingo_swap"

    defaultConfig {
        applicationId = "com.lighthouse.lingo_swap"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.appVersion.get()

        buildConfigField(
            "String",
            "LIGHTHOUSE_BASE_URL",
            properties.getProperty("LIGHTHOUSE_BASE_URL")
        )

        buildConfigField(
            "String",
            "TEST_BASE_URL",
            properties.getProperty("TEST_BASE_URL")
        )
    }

    buildTypes {
//        debug {
//
//        }
        release {
            isMinifyEnabled = true // APK or AAB
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:home"))
    implementation(project(":feature:chats"))
    implementation(project(":feature:board"))
    implementation(project(":feature:profile"))
    implementation(project(":navigation"))

    implementation(libs.hilt)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.gson)
    implementation(libs.bundles.room)
    kapt(libs.room.complier)
    implementation(libs.bundles.navigation)
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.6")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
}