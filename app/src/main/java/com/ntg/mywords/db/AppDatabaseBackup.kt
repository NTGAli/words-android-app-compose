package com.ntg.mywords.db

import android.content.Context
import androidx.room.Room
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AppDatabaseBackup {
    companion object {
        fun backupDatabase(context: Context, dbName: String) {
            val appDatabase = Room.databaseBuilder(context, AppDB::class.java, dbName)
                .build()

            val databaseFile = context.getDatabasePath(dbName)
            val backupFile = File(context.getExternalFilesDir(null), "backup_$dbName")

            try {
                FileInputStream(databaseFile).channel.use { input ->
                    FileOutputStream(backupFile).channel.use { output ->
                        output.transferFrom(input, 0, input.size())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            appDatabase.close()
        }


        fun restoreDatabase(context: Context, dbName: String) {
            val appDatabase = Room.databaseBuilder(context, AppDB::class.java, dbName)
                .build()

            val backupFile = File(context.getExternalFilesDir(null), "backup_$dbName")
            val databaseFile = context.getDatabasePath(dbName)

            try {
                FileInputStream(backupFile).channel.use { input ->
                    FileOutputStream(databaseFile).channel.use { output ->
                        output.transferFrom(input, 0, input.size())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            appDatabase.close()
        }
    }
}
