package com.ntg.vocabs.db

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ntg.vocabs.util.timber

class AutoInsertWorkerFactory (private val appDB: AppDB) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName){
            AutoInsertWorker::class.java.name -> {
                AutoInsertWorker(appContext, workerParameters, appDB)
            }

            else -> null
        }
    }
}