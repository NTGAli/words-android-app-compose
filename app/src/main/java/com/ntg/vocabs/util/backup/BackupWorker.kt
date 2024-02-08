package com.ntg.vocabs.util.backup

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ntg.vocabs.R
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.model.DriveBackup
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.getCurrentDate
import com.ntg.vocabs.util.isInternetAvailable
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class BackupWorker(
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

        if (isInternetAvailable(appContext)) {
            GoogleSignIn.getLastSignedInAccount(appContext)?.let { googleAccount ->


                // get credentials
                val credential = GoogleAccountCredential.usingOAuth2(
                    appContext, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account!!

                val transport: HttpTransport = NetHttpTransport()
                val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()


                // get Drive Instance
                val drive = Drive
                    .Builder(
                        transport,
                        jsonFactory,
                        credential
                    )
                    .setApplicationName(appContext.getString(R.string.app_name))
                    .build()


//                val dataFolder = File(appContext.getExternalFilesDir(""), "backups")
//                val file = File(dataFolder, Constant.BACKUPS)

                backupOnDrive(appContext, drive) {
                    appDB.getDriveBackup()
                        .insert(DriveBackup(0, it, System.currentTimeMillis().toString(), "---"))

                }


            }
            return Result.success()
        } else {
            appDB.getDriveBackup().insert(
                DriveBackup(
                    0,
                    false,
                    System.currentTimeMillis().toString(),
                    "internet error"
                )
            )
            return Result.failure()
        }


    }


    private suspend fun backupOnDrive(
        context: Context,
        drive: Drive,
        state: suspend (Boolean) -> Unit
    ) {
        try {
            val isBackupFileExist = File(context.getExternalFilesDir("backups"), "backup").exists()
            if (isBackupFileExist) {
                zipFolder(
                    context.getExternalFilesDir("backups")?.path.toString(),
                    context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}"
                ).also {
                    backupDB(
                        File(context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}"),
                        drive,
                        state
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun backupDB(file: File, drive: Drive, state: suspend (Boolean) -> Unit) {

        try {
            val folderId = getFolderId(drive)
            if (folderId.isNullOrEmpty()) {
                state.invoke(false)
                return
            }

            val gfile = com.google.api.services.drive.model.File()

            val fileContent = FileContent("application/octet-stream", file)
            val backupName = "VocabsBackup_${getCurrentDate()}"
            gfile.name = backupName

            val parents: MutableList<String> = ArrayList(1)
            parents.add(folderId) // Here you need to get the parent folder id

            gfile.parents = parents

            try {
                drive.Files().create(gfile, fileContent).setFields("id").execute()
                removeOldBackups(drive,backupName, folderId)
                state.invoke(true)
            } catch (e: Exception) {
                state.invoke(false)
            }
        } catch (e: Exception) {
            state.invoke(false)
        }
    }

    private suspend fun removeOldBackups(drive: Drive,lastBackupName: String, folderId: String?) {
//            val folderId = getFolderId()

        if (folderId != null) {
            // List files in the folder
            val result = drive.files().list()
                .setQ("'$folderId' in parents")
                .execute()

            val files = result.files

            for (file in files) {
                val fileName = file.name

                if (fileName != lastBackupName && fileName.startsWith("VocabsBackup_")) {
                    // Delete the file
                    drive.files().delete(file.id).execute()
                }
            }
        }
    }

    private fun getFolderId(drive: Drive): String? {
        val query =
            "mimeType='application/vnd.google-apps.folder' and name='${Constant.VOCAB_FOLDER_NAME_DRIVE}'"
        val result = drive.files().list().setQ(query).execute()

        return result.files?.firstOrNull()?.id
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