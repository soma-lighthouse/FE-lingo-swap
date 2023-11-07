package com.lighthouse.auth.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/** Milliseconds used for UI animations */
const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

/**
 * Simulate a button click, including a small delay while it is being pressed to trigger the
 * appropriate animations.
 */
fun ImageButton.simulateClick(delay: Long = ANIMATION_FAST_MILLIS) {
    performClick()
    isPressed = true
    invalidate()
    postDelayed({
        invalidate()
        isPressed = false
    }, delay)
}

fun hasPermissions(context: Context) =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


fun getOutputDirectory(context: Context): File {
    val appContext = context.applicationContext
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(
            it,
            appContext.resources.getString(com.lighthouse.android.common_ui.R.string.app_name)
        ).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}

fun createFile(baseFolder: File, format: String, extension: String): File {
    return File(
        baseFolder, SimpleDateFormat(format, Locale.KOREA)
            .format(System.currentTimeMillis()) + extension
    )
}

@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

    /** Helper method that applies padding from cutout's safe insets */
    fun doPadding(cutout: DisplayCutout) = setPadding(
        cutout.safeInsetLeft,
        cutout.safeInsetTop,
        cutout.safeInsetRight,
        cutout.safeInsetBottom
    )

    // Apply padding using the display cutout designated "safe area"
    rootWindowInsets?.displayCutout?.let { doPadding(it) }

    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}

/** Same as [AlertDialog.show] but setting immersive mode in the dialog's window */
fun AlertDialog.showImmersive() {
    // Set the dialog to not focusable
    window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    )

    // Make sure that the dialog's window is in full screen
    window?.decorView?.systemUiVisibility = FLAGS_FULLSCREEN

    // Show the dialog while still in immersive mode
    show()

    // Set the dialog to focusable again
    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}