plugins {
    kotlin("android")
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.lighthouse.android.data"

    buildTypes {
        debug {
            isMinifyEnabled = false // APK or AAB
            consumerProguardFile("proguard-rules.pro")
        }

        release {
            isMinifyEnabled = true
            consumerProguardFile("proguard-rules.pro")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.hilt)
    implementation(libs.firebase.bug.fix)
    implementation(libs.firebase.config)
    kapt(libs.hilt.kapt)
    api(libs.bundles.basic.test)
    api(libs.bundles.retrofit)
    api(libs.bundles.okhttp)
    api(libs.bundles.gson)
}