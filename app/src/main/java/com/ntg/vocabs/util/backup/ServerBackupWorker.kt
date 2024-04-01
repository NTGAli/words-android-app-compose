package com.ntg.vocabs.util.backup

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.model.DriveBackup
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ServerBackupWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val appDB = Room.databaseBuilder(
            context = appContext,
            AppDB::class.java,
            Constant.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        val email =  inputData.getString("email")
        timber("SERVER_BACKUP_ERR :::: $email")

        backupToServer(appContext,email){
            appDB.getDriveBackup()
                .insert(DriveBackup(0, it, System.currentTimeMillis().toString(), "---"))
        }

        return Result.success()
    }

    private suspend fun backupToServer(
        context: Context,
        email: String?,
        onSuccess: suspend (Boolean) -> Unit
    ){
        val storage = Firebase.storage

        if (email != null){
            try {

                val isBackupFileExist = File(appContext.getExternalFilesDir("backups"), "backup").exists()
                if (isBackupFileExist){
                    val storageRef = storage.reference.child("${email}/backup.zip")

                    zipFolder(
                        context.getExternalFilesDir("backups")?.path.toString(),
                        context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}"
                    ).also {

                        val backupFile = File(context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}")
                        val fileUri = Uri.fromFile(backupFile)

                        val metadata = StorageMetadata.Builder()
                            .setContentType("application/zip")
                            .build()

                        storageRef.putFile(fileUri, metadata)
                            .addOnSuccessListener {
                                CoroutineScope(Dispatchers.IO).launch {
                                    timber("SERVER_BACKUP_ERR :::: SSSS")
                                    onSuccess.invoke(true)
                                }
                            }
                            .addOnFailureListener { exception ->
                                timber("SERVER_BACKUP_ERR :::: ${exception.message}")
                                CoroutineScope(Dispatchers.IO).launch {
                                    onSuccess.invoke(false)
                                }
                            }
                    }



                }


            }catch (e: Exception){
                timber("SERVER_BACKUP_ERR :::: ${e.message}")
                onSuccess.invoke(false)
            }
        }else{
            timber("SERVER_BACKUP_ERR :::: ")
            onSuccess.invoke(false)
        }
    }

    private fun zipFolder(sourceFolder: String, zipFilePath: String) {
        val sourceFile = File(sourceFolder)
        val zipFile = File(zipFilePath)

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOutputStream ->
            zip(sourceFile, sourceFile.name, zipOutputStream)
        }

        println("Folder $sourceFolder zipped successfully to $zipFilePath")
    }

    private fun zip(fileToZip: File, fileName: String, zipOutputStream: ZipOutputStream) {
        if (fileToZip.isHidden) {
            return
        }

        if (fileToZip.isDirectory) {
            val children = fileToZip.listFiles() ?: return
            for (childFile in children) {
                zip(childFile, "$fileName/${childFile.name}", zipOutputStream)
            }
        } else {
            FileInputStream(fileToZip).use { fileInputStream ->
                val entry = ZipEntry(fileName)
                zipOutputStream.putNextEntry(entry)

                val buffer = ByteArray(1024)
                var len: Int
                while (fileInputStream.read(buffer).also { len = it } > 0) {
                    zipOutputStream.write(buffer, 0, len)
                }

                zipOutputStream.closeEntry()
            }
        }
    }
}