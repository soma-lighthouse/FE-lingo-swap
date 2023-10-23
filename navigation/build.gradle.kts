plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.navigation"

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
}

dependencies {
    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.material)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
}