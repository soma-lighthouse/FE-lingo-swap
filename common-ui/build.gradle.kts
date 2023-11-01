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
        debug {
            consumerProguardFile("proguard-rules.pro")
        }

        release {
            consumerProguardFile("proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

}

dependencies {
    api(project(":domain"))
    implementation(project(":navigation"))


    implementation(libs.bundles.image)
    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.google.admob)
    implementation(libs.sendbird)
    implementation(libs.bundles.firebase)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
}