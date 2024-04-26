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

class MediaBackupWorker(
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

        File(appContext.getExternalFilesDir(""), IMAGES).deleteRecursively()
        File(appContext.getExternalFilesDir(""), Voices).deleteRecursively()

        appDB.wordDao().getUnSyncedMedia().let {list ->
            timber("getUnSyncedMedia ::::: ${list.map { it.word }}")
           if (list.isNotEmpty()){
               val imagesFile = copyFilesToFolder(appContext, list.filter { it.imageSynced != null && it.images.orEmpty().isNotEmpty() }.map { it.images.orEmpty()[0] }, IMAGES)
               val voicesFile = copyFilesToFolder(appContext, list.filter { it.voiceSynced != null && it.voice.orEmpty().isNotEmpty() }.map { it.voice.orEmpty() }, Voices)
               val zipFile = zipFolders(appContext, listOf(IMAGES, Voices), "MEDIA_BACKUP")
               backupToServer(appContext,email,zipFile){isSuccess ->
                   if (isSuccess){
                       list.forEach { word ->
                           if (word.voiceSynced != null) appDB.wordDao().voiceSynced(word.id)
                           if (word.imageSynced != null) appDB.wordDao().imageSynced(word.id)
                       }
                   }
                   imagesFile.deleteRecursively()
                   voicesFile.deleteRecursively()
                   zipFile.delete()
               }
           }
        }

//        backupToServer(appContext,email){
//            appDB.getDriveBackup()
//                .insert(DriveBackup(0, it, System.currentTimeMillis().toString(), "---"))
//        }

        return Result.success()
    }


    private fun copyFilesToFolder(context: Context, sourcePaths: List<String>, destinationPath: String): File {
        val destinationDir = File(context.getExternalFilesDir(""), destinationPath)

        // Create destination directory if it doesn't exist
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }

        // Copy files from source paths to destination directory
        sourcePaths.forEach { sourcePath ->
            val sourceFile = File(sourcePath)
            if (sourceFile.exists()) {
                val outputFile = File(destinationDir, sourceFile.name)
                sourceFile.copyTo(outputFile, overwrite = true)
            }
        }
        return destinationDir
    }

    private fun zipFolders(context: Context, folders: List<String>, zipFileName: String): File {
        val zipFile = File(context.getExternalFilesDir(""), zipFileName)

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOutputStream ->
            folders.forEach { folderPath ->
                val folder = File(context.getExternalFilesDir(""), folderPath)
                folder.listFiles()?.forEach { file ->
                    timber("getUnSyncedMedia ---> $file")
                    val entry = ZipEntry("$folderPath/${file.name}")
                    zipOutputStream.putNextEntry(entry)
                    FileInputStream(file).use { input ->
                        input.copyTo(zipOutputStream)
                    }
                    zipOutputStream.closeEntry()
                }
            }
        }
        return zipFile
    }

    private suspend fun backupToServer(
        context: Context,
        email: String?,
        zipFile: File,
        onSuccess: suspend (Boolean) -> Unit
    ){
        val storage = Firebase.storage

        if (email != null){
            try {

//                val isBackupFileExist = File(appContext.getExternalFilesDir("backups"), "backup").exists()
                val isBackupFileExist = zipFile.exists()
                if (isBackupFileExist){
                    val storageRef = storage.reference.child("${email}/backup${System.currentTimeMillis()}.zip")

                    val fileUri = Uri.fromFile(zipFile)

                    val metadata = StorageMetadata.Builder()
                        .setContentType("application/zip")
                        .build()

                    storageRef.putFile(fileUri, metadata)
                        .addOnSuccessListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                timber("SERVER_BACKUP_ERR :::: SSSS")
                                onSuccess.invoke(true)
//                                zipFile.delete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            timber("SERVER_BACKUP_ERR :::: ${exception.message}")
                            CoroutineScope(Dispatchers.IO).launch {
                                onSuccess.invoke(false)
//                                zipFile.delete()
                            }
                        }

//                    zipFolder(
//                        context.getExternalFilesDir("backups")?.path.toString(),
//                        context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}"
//                    ).also {
//
//                        val backupFile = File(context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}")
//                        val fileUri = Uri.fromFile(backupFile)
//
//                        val metadata = StorageMetadata.Builder()
//                            .setContentType("application/zip")
//                            .build()
//
//                        storageRef.putFile(fileUri, metadata)
//                            .addOnSuccessListener {
//                                CoroutineScope(Dispatchers.IO).launch {
//                                    timber("SERVER_BACKUP_ERR :::: SSSS")
//                                    onSuccess.invoke(true)
//                                }
//                            }
//                            .addOnFailureListener { exception ->
//                                timber("SERVER_BACKUP_ERR :::: ${exception.message}")
//                                CoroutineScope(Dispatchers.IO).launch {
//                                    onSuccess.invoke(false)
//                                }
//                            }
//                    }



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

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.listFiles() != null){
            if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteRecursive(
                child
            )
            fileOrDirectory.delete()
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

const val IMAGES = "images"
const val Voices = "voices"
