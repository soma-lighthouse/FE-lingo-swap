package com.lighthouse.android.common_ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.lighthouse.android.common_ui.R


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