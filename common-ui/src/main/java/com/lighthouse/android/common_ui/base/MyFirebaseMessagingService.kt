package com.lighthouse.android.common_ui.base

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.RemoteMessage
import com.lighthouse.android.common_ui.util.StringSet
import com.sendbird.android.SendbirdChat.markAsDelivered
import com.sendbird.android.push.SendbirdPushHandler
import com.sendbird.uikit.activities.ChannelListActivity.newRedirectToChannelIntent
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference


class MyFirebaseMessagingService : SendbirdPushHandler() {

    override fun onMessageReceived(context: Context, remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Messaging", "Message data payload: ${remoteMessage.data}")
        }

        Log.d("TESTING START", "enter")
        try {
            if (remoteMessage.data.containsKey("sendbird")) {
                val jsonStr = remoteMessage.data["sendbird"]
                markAsDelivered(remoteMessage.data)
                if (jsonStr == null) return
                sendNotification(context, JSONObject(jsonStr))

            }
        } catch (e: JSONException) {
            Log.e("TESTING", "JSONException: ${e.message}")
        }
    }

    override val isUniquePushToken: Boolean
        get() = false

    override fun onNewToken(newToken: String?) {
        Log.d(TAG, "onNewToken($newToken)")
        pushToken.set(newToken)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private val pushToken = AtomicReference<String?>()

        fun sendNotification(context: Context, sendBird: JSONObject) {
            val message = sendBird.getString(StringSet.message)
            val channel = sendBird.getJSONObject(StringSet.channel)
            val channelUrl = channel.getString(StringSet.channel_url)
            val messageId = sendBird.getLong(StringSet.message_id)
            var senderName = context.getString(com.lighthouse.android.common_ui.R.string.app_name)
            if (sendBird.has(StringSet.sender)) {
                val sender = sendBird.getJSONObject(StringSet.sender)
                senderName = sender.getString(StringSet.name)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val CHANNEL_ID = StringSet.CHANNEL_ID
            if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
                val mChannel =
                    NotificationChannel(
                        CHANNEL_ID,
                        StringSet.CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                notificationManager.createNotificationChannel(mChannel)
            }
            val intent = newRedirectToChannelIntent(context, channelUrl)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            @SuppressLint("UnspecifiedImmutableFlag")
            val pendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.getActivity(
                    context,
                    messageId.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                ) else PendingIntent.getActivity(context, messageId.toInt(), intent, 0)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.lighthouse.android.common_ui.R.drawable.logo)
                .setColor(
                    ContextCompat.getColor(
                        context,
                        com.lighthouse.android.common_ui.R.color.main
                    )
                ) // small icon background color
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        com.lighthouse.android.common_ui.R.drawable.logo
                    )
                )
                .setContentTitle(senderName)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
            notificationBuilder.setContentText(message)
            notificationManager.notify(
                System.currentTimeMillis().toString(),
                0,
                notificationBuilder.build()
            )
        }
    }

}