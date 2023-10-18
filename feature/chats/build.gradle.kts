plugins {
    kotlin("android")
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.android.chats"

    buildTypes {
        debug {
            isMinifyEnabled = true // APK or AAB
            consumerProguardFile("proguard-rules.pro")
        }

        release {
            isMinifyEnabled = true
            consumerProguardFile("proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":common-ui"))
    implementation(project(":navigation"))

    implementation("com.sendbird.sdk:uikit:3.+")
    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.bundles.android.basic.ui)
    implementation(libs.kotlin.coroutines)
    implementation(libs.hilt)
    implementation("androidx.appcompat:appcompat:1.6.1")
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.image)
}