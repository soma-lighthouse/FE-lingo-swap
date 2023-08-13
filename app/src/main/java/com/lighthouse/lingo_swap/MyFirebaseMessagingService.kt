package com.lighthouse.lingo_swap

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.SendbirdChat
import com.sendbird.android.push.PushTokenRegistrationStatus
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val channelId = "channel_id"
    private val channelName = "Sendbird Channel"
    private val channelDescription = "Notification_Channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Messaging", "Message data payload: ${remoteMessage.data}")
        }

        if (remoteMessage.data.containsKey("sendbird")) {
            val sendbird = JSONObject(remoteMessage.getData().get("sendbird"))
            val channel = sendbird.get("channel") as JSONObject
            val channelUrl = channel["channel_url"] as String
            val messageTitle = sendbird.get("push_title").toString()
            val messageBody = sendbird.get("message") as String
            sendNotification(applicationContext, messageTitle, messageBody)
        }

    }

    private fun sendNotification(
        context: Context,
        messageTitle: String?,
        messageBody: String,
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    com.lighthouse.android.common_ui.R.drawable.kr
                )
            )
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .build()

        context.createNotificationChannel()

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TOKEN", "newToken: ${token}")
        SendbirdChat.registerPushToken(token) { status, e ->
            if (e != null) {
                e.printStackTrace()
                return@registerPushToken
            }

            if (status == PushTokenRegistrationStatus.PENDING) {
                //token registration is pending
            }

        }
    }

    private fun Context.createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}