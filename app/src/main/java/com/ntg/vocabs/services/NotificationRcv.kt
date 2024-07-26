package com.ntg.vocabs.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ntg.vocabs.MainActivity
import com.ntg.vocabs.R
import com.ntg.vocabs.util.timber

const val notificationID = "notificationID"
const val channelID = "channel342341"

class NotificationRcv : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        timber("NotificationRcv ::::: START")

        val notificationId = intent.getIntExtra("notificationID", -1)
        val channelID = notificationId.toString() // Ensure channelID is defined

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Vocab_CHANNEL"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, channelName, importance).apply {
                description = "VOCAB_CHANNEL_DESCRIPTION"
            }

            // Register the channel with the system
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            // Logging to ensure the channel is created
            val existingChannel = manager.getNotificationChannel(channelID)
            if (existingChannel == null) {
                timber("NotificationRcv ::: Failed to create notification channel")
            } else {
                timber("NotificationRcv ::: Notification channel created: ${existingChannel.id}")
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra("titleExtra"))
            .setContentText(intent.getStringExtra("messageExtra"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}
