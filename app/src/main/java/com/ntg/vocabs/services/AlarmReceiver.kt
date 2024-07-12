package com.ntg.vocabs.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ntg.vocabs.util.timber

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        timber("ReminderService ::: RCV")

        val id = intent?.getIntExtra("id",0)
        val word = intent?.getStringExtra("word")
        val type = intent?.getStringExtra("type")
        val isServiceRunning = intent?.getBooleanExtra("isServiceRunning", false)

        val reminderServiceIntent = Intent(context, ReminderService::class.java)
        reminderServiceIntent.putExtra("id", id)
        reminderServiceIntent.putExtra("word", word)
        reminderServiceIntent.putExtra("type", type)
        timber("ReminderService ::: $isServiceRunning")
        if (!isServiceRunning!!) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(reminderServiceIntent);
            }else {
                context.startService(reminderServiceIntent);
            }
        }
    }
}