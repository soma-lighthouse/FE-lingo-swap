plugins {
    kotlin("android")
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.android.home"

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
    implementation(project(":navigation"))

    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.6.0")

    implementation(libs.bundles.androidx.ui.foundation)
    implementation(libs.google.admob)
    implementation(libs.bundles.android.basic.ui)
    implementation(libs.kotlin.coroutines)
    implementation(libs.hilt)
    kapt(libs.hilt.kapt)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.image)
}