plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.lighthouse.android.common_ui"

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        dataBinding = true
    }

}

dependencies {
    api(project(":domain"))
    implementation(project(":navigation"))

    implementation("com.sendbird.sdk:uikit:3.+")

    implementation(libs.bundles.image)
    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.google.admob)
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation(libs.bundles.firebase)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
}