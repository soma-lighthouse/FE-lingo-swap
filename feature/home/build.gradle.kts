plugins {
    kotlin("android")
    id("com.android.library")
    kotlin("kapt")
}

android {
    namespace = "com.lighthouse.android.home"

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
    implementation(project(":common-ui"))

    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.kotlin.coroutines)
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
}