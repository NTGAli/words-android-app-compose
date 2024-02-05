package com.ntg.vocabs.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ntg.vocabs.MainActivity
import com.ntg.vocabs.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        timber("MyFirebaseMessagingService ::: FROM : ${remoteMessage.from}")
        if (remoteMessage.notification?.title.orEmpty().isNotEmpty()) {
            sendNotification(
                remoteMessage.notification?.title.orEmpty(),
                remoteMessage.notification?.body.orEmpty(),
                remoteMessage.data["action"].orEmpty()
            )
        }
    }

    override fun onNewToken(token: String) {
        timber("FCM_TOKEN :::: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(title: String, messageBody: String, action: String) {

        val intent = if (action.startsWith("http")){
            Intent(Intent.ACTION_VIEW)
        }else Intent(this, MainActivity::class.java)

        if (action.startsWith("http")){
            intent.data  = Uri.parse(action)
        }else{
        intent.putExtra(Constant.ACTION, action)
        intent.action = action
        }
        timber("CUSTOM_DATA_NOTIFICATION ::::::::::::::: $action")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icons_v)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}