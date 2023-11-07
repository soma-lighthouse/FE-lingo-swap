package com.lighthouse.lingo_talk

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LingoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}
