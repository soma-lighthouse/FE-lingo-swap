plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.board"

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

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.6.0")

    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.bundles.android.basic.ui)
    implementation(libs.kotlin.coroutines)
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.image)
}