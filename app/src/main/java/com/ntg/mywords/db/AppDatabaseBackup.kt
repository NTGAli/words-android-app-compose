package com.ntg.mywords.db

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.room.Room
import java.io.*

class AppDatabaseBackup {
    companion object {
        fun backupDatabase(context: Context, backupUri: Uri? = null) {



//            Toast.makeText(context, "Data saved publicly..", Toast.LENGTH_SHORT).show()
//
//
//            val appDatabase = Room.databaseBuilder(context, AppDB::class.java, Constant.DATABASE_NAME)
//                .build()
//
//            val databaseFile = context.getDatabasePath(Constant.DATABASE_NAME)
//
//            try {
//                context.contentResolver.openFileDescriptor(backupUri.path.orEmpty().toUri(), "w")?.use { parcelFileDescriptor ->
//                    val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
//                    FileInputStream(databaseFile).channel.use { input ->
//                        fileOutputStream.channel.use { output ->
//                            output.transferFrom(input, 0, input.size())
//                        }
//                    }
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            appDatabase.close()
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
