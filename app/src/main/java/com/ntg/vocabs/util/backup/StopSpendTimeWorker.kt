package com.ntg.vocabs.util.backup

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.util.Constant

class StopSpendTimeWorker (
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appDB = Room.databaseBuilder(
            context = applicationContext, AppDB::class.java, Constant.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
        appDB.timeSpentDao().stopTime(System.currentTimeMillis())
        return Result.success()
    }
}