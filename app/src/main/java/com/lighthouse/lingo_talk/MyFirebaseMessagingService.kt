package com.lighthouse.lingo_talk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.util.LocalKey
import com.lighthouse.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var local: LocalPreferenceDataSource

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        local.save(LocalKey.PUSH_TOKEN, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("TESTING PUSH", "onMessageReceived: ${message.data}")
        if (message.data.isNotEmpty() && local.getBoolean(LocalKey.PUSH_ENABLED)) {
            sendNotification(message)
        }
    }

    private fun sendNotification(message: RemoteMessage) {
        val channelId = StringSet.CHANNEL_ID
        val channelName = StringSet.CHANNEL_NAME
        val channelDescription = StringSet.CHANNEL_DESCRIPTION
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH // 중요도 (HIGH: 상단바 표시 가능)
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }

        val uniID: Int = (System.currentTimeMillis() / 7).toInt()
        val intent = Intent(this, AuthActivity::class.java)
        for (key in message.data.keys) {
            intent.putExtra(key, message.data[key])
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            uniID,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.lighthouse.android.common_ui.R.drawable.logo)
            .setColor(
                ContextCompat.getColor(
                    applicationContext,
                    com.lighthouse.android.common_ui.R.color.main
                )
            ) // small icon background color
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    com.lighthouse.android.common_ui.R.drawable.logo
                )
            )
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        notificationManager.notify(uniID, notificationBuilder.build())
    }

    fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("TESTING PUSH", "token: $it")
        }
    }
}
