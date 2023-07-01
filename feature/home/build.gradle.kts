plugins {
    kotlin("android")
    id("com.android.library")
    id("dagger.hilt.android.plugin")
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

//kapt {
//    javacOptions {
//        // These options are normally set automatically via the Hilt Gradle plugin, but we
//        // set them manually to workaround a bug in the Kotlin 1.5.20
//        option("-Adagger.fastInit=ENABLED")
//        option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
//    }
//}

dependencies {
    implementation(project(":common-ui"))

    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.kotlin.coroutines)
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
}