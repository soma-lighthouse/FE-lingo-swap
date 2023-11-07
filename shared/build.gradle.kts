plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.lighthouse.android.shared"

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    

    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.material)
}