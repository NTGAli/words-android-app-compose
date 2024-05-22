package com.ntg.vocabs.util.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ntg.vocabs.R

class ReviewWorker (
    private val appContext: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val word = inputData.getString("word")
        return if (word != null){
            showNotification(word)
            Result.success()
        }else{
            Result.failure()
        }
    }

    private fun showNotification(word: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "worker_channel",
                "Worker Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "worker_channel")
            .setContentTitle(appContext.getString(R.string.app_name))
            .setContentText(appContext.getString(R.string.lets_review_format, word))
            .setSmallIcon(R.drawable.vocabs)
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}