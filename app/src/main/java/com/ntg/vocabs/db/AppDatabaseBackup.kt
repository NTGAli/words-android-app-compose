package com.ntg.vocabs.db

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.timber
import java.io.*

class AppDatabaseBackup {
    companion object {
        fun backupDatabase(context: Context, backupUri: Uri? = null) {

            val appDatabase = Room.databaseBuilder(context, AppDB::class.java, Constant.DATABASE_NAME)
                .build()

            val databaseFile = context.getDatabasePath(Constant.DATABASE_NAME)
            val backupFile = File(context.getExternalFilesDir(null), "backup_${Constant.DATABASE_NAME}")

            try {
                FileInputStream(databaseFile).channel.use { input ->
                    FileOutputStream(backupFile).channel.use { output ->
                        output.transferFrom(input, 0, input.size())
                    }
                }

                val inputString = databaseFile.bufferedReader().use { it.readText() }
                timber("ajhfjkawhfkjhwajkfhwjkafhk $inputString")
//                println(inputString)

//                timber("ajhfjkawhfkjhwajkfhwjkafhk ${databaseFile.absoluteFile}")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            appDatabase.close()

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
