package com.lighthouse.lingo_swap

import android.app.Application
import android.content.Intent
import android.os.Process
import com.lighthouse.auth.AuthActivity
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class LingoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

//        Thread.setDefaultUncaughtExceptionHandler { _, _ -> caughtException() }
    }

    private fun caughtException() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)

        Process.killProcess(Process.myPid())
        exitProcess(-1)
    }


}

