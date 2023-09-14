package com.lighthouse.android.chats.uikit.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.lighthouse.android.chats.R
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.SendingStatus
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.utils.DrawableUtils

fun drawStatus(view: ImageView, message: BaseMessage, channel: BaseChannel): Int {
    val context: Context = view.context

    return when (message.sendingStatus) {
        SendingStatus.SUCCEEDED -> {
            if (channel is GroupChannel) {
                val unreadMemberCount = channel.getUnreadMemberCount(message)
                val unDeliveredMemberCount = channel.getUndeliveredMemberCount(message)
                if (unreadMemberCount == 0) {
                    drawRead(view, context)
                } else if (unDeliveredMemberCount == 0) {
                    drawDelivered(view, context)
                } else {
                    drawSent(view, context)
                }
            }
            View.VISIBLE
        }

        else -> View.GONE
    }
}

fun drawRead(view: ImageView, context: Context) {

    view.setImageDrawable(
        AppCompatResources.getDrawable(context, R.drawable.read)
    )
}

fun drawDelivered(view: ImageView, context: Context) {
    view.setImageDrawable(
        AppCompatResources.getDrawable(context, R.drawable.unread)
    )
}

fun drawSent(view: ImageView, context: Context) {
    view.setImageDrawable(
        DrawableUtils.setTintList(
            context,
            com.sendbird.uikit.R.drawable.icon_done,
            SendbirdUIKit.getDefaultThemeMode().monoTintResId
        )
    )
}