plugins {
    kotlin("android")
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.lighthouse.lingo_talk"

    defaultConfig {
        applicationId = "com.lighthouse.lingo_talk"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false // APK or AAB
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "ad_application_id", "ca-app-pub-3940256099942544~3347511713")
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "ad_application_id", "ca-app-pub-7050097872547694~2687354079")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
        }
    }


    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":common-ui"))
    implementation(project(":feature:home"))
    implementation(project(":feature:chats"))
    implementation(project(":feature:board"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:auth"))
    implementation(project(":navigation"))
    implementation(project(":swm-logging"))

    kapt(libs.hilt.kapt)
    implementation(libs.hilt)
    implementation(libs.shared.preference.security)
    implementation(libs.bundles.firebase)
    implementation(libs.google.services)
    implementation(libs.google.login)
    implementation(libs.google.admob)
}