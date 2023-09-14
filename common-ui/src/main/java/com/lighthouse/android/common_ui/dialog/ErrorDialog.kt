package com.lighthouse.android.common_ui.dialog

import android.R
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


fun showOKDialog(
    context: Context,
    title: String,
    message: String,
    positiveListener: DialogInterface.OnClickListener? = null,
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(R.string.ok, positiveListener)
    builder.setNegativeButton(R.string.cancel, null)
    builder.show()
}
