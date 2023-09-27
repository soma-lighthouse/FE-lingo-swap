package com.lighthouse.lingo_swap

import android.app.Application
import android.content.Intent
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.lighthouse.auth.AuthActivity
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class LingoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Thread.setDefaultUncaughtExceptionHandler { _, e -> caughtException(e) }
    }

    private fun caughtException(e: Throwable) {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        Log.e("LingoApplication", e.stackTraceToString())

        startActivity(intent)

        Process.killProcess(Process.myPid())
        exitProcess(-1)
    }
}
