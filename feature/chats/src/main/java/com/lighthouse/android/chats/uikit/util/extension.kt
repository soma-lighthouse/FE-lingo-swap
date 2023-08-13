package com.lighthouse.android.chats.uikit.util

import android.content.Context
import android.widget.ImageView
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.SendingStatus
import com.sendbird.uikit.utils.DrawableUtils

fun drawStatus(view: ImageView, message: BaseMessage) {
    val context: Context = view.context

    val drawableResId: Int
    val tintResId: Int

    when (message.sendingStatus) {
        SendingStatus.SUCCEEDED -> {
            drawableResId = com.sendbird.uikit.R.drawable.icon_done
            tintResId = com.sendbird.uikit.R.color.secondary_300
        }

        SendingStatus.PENDING -> {
            drawableResId = com.sendbird.uikit.R.drawable.sb_message_progress
            tintResId = com.sendbird.uikit.R.color.primary_300
        }

        else -> {
            drawableResId = com.sendbird.uikit.R.drawable.icon_error
            tintResId = com.sendbird.uikit.R.color.error_300
        }
    }

    view.setImageDrawable(
        DrawableUtils.setTintList(
            context,
            drawableResId,
            tintResId
        )
    )
}