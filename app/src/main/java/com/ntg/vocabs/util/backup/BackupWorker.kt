package com.ntg.vocabs.util.backup

import android.content.Context
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
import com.ntg.vocabs.util.isInternetAvailable
import java.io.File

class BackupWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appDB =  Room.databaseBuilder(
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


                val dataFolder = File(appContext.getExternalFilesDir(""), "backups")
                val file = File(dataFolder, Constant.BACKUPS)

                backupDB(file, drive){
                    appDB.getDriveBackup().insert(DriveBackup(0,it,System.currentTimeMillis().toString(),"---"))

                }


            }
            return Result.success()
        }else{
            appDB.getDriveBackup().insert(DriveBackup(0,false,System.currentTimeMillis().toString(),"internet error"))
            return Result.failure()
        }


    }


    private suspend fun backupDB(file: File, drive: Drive, state:suspend (Boolean) -> Unit) {

        try {
            val folderId = getFolderId(drive)
            if (folderId.isNullOrEmpty()) {
                state.invoke(false)
                return
            }

            val gfile = com.google.api.services.drive.model.File()

            val fileContent = FileContent("application/octet-stream", file)
            gfile.name = "VocabsBackup_${System.currentTimeMillis()}"

            val parents: MutableList<String> = ArrayList(1)
            parents.add(folderId) // Here you need to get the parent folder id

            gfile.parents = parents

            try {
                drive.Files().create(gfile, fileContent).setFields("id").execute()
                state.invoke(true)
            }catch (e: Exception){
                state.invoke(false)
            }
        }catch (e:Exception){
            state.invoke(false)
        }
    }

    private suspend fun getFolderId(drive: Drive): String? {
        val query =
            "mimeType='application/vnd.google-apps.folder' and name='${Constant.VOCAB_FOLDER_NAME_DRIVE}'"
        val result = drive.files().list().setQ(query).execute()

        return result.files?.firstOrNull()?.id
    }
}