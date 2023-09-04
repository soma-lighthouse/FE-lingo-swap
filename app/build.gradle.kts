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
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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

        buildConfigField(
            "String",
            "SENDBIRD_APPLICATION_ID",
            properties.getProperty("SENDBIRD_APPLICATION_ID")
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
    implementation("com.sendbird.sdk:uikit:3.+")
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":common-ui"))
    implementation(project(":feature:home"))
    implementation(project(":feature:chats"))
    implementation(project(":feature:board"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:auth"))
    implementation(project(":navigation"))

    implementation(libs.hilt)
    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.bundles.android.basic.ui)
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.gson)
    implementation(libs.bundles.room)
    kapt(libs.room.complier)
    implementation(libs.bundles.navigation)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.shared.preference.security)
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation(libs.google.login)
    implementation(libs.bundles.firebase)
}