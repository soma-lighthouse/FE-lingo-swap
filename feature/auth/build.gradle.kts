plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.auth"

    defaultConfig {
        buildConfigField("String", "CURRENT_VER", "\"${libs.versions.appVersion.get()}\"")
    }

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
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":common-ui"))
    implementation(project(":navigation"))

    api(libs.bundles.androidx.ui.foundation)
    api(libs.bundles.android.basic.ui)
    api(libs.bundles.navigation)
    kapt(libs.hilt.kapt)
    implementation(libs.hilt)
    implementation(libs.google.login)
    implementation(libs.guava)
    implementation(libs.splash.screen)
    implementation(libs.firebase.config)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.image)
    implementation(libs.bundles.camerax)

    implementation("com.google.apis:google-api-services-people:v1-rev20210419-1.31.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
}