package com.lighthouse.android.common_ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.lighthouse.android.common_ui.R
import java.util.concurrent.CountDownLatch


fun showOKDialog(
    context: Context,
    title: String,
    message: String,
    negative: Boolean = true,
    positiveListener: DialogInterface.OnClickListener? = null,
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(R.string.ok, positiveListener)
    if (negative) {
        builder.setNegativeButton(R.string.cancel, null)
    }
    builder.show()
}

fun showUpdateDialog(
    context: Context,
    title: String,
    message: String,
    updateURL: String,
    okAction: () -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setCancelable(false)
    builder.setPositiveButton(R.string.update_title) { _, _ ->
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(updateURL)
            )
        )
        okAction()
    }
    builder.show()
}

fun showBlockingDialog(context: Context) {
    // Create a CountDownLatch with a count of 1
    val latch = CountDownLatch(1)

    // Create and show the dialog
    val alertDialog = AlertDialog.Builder(context)
        .setTitle("No Internet Connection")
        .setMessage("Please check your internet connection and try again.")
        .setPositiveButton("OK") { _, _ ->
            latch.countDown()
        }
        .setCancelable(false)
        .create()

    alertDialog.show()

    try {
        latch.await()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

    alertDialog.dismiss()

    // Continue with other processes or tasks
    Handler(Looper.getMainLooper()).post {
        System.exit(0)
    }
}