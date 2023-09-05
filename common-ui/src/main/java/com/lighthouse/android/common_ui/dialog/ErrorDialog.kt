package com.lighthouse.android.common_ui.dialog

import android.R
import android.content.Context
import androidx.appcompat.app.AlertDialog


fun showOKDialog(context: Context, title: String, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(R.string.ok, null)
    builder.show()
}
