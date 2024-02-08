package com.ntg.vocabs.vm

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import com.ntg.vocabs.R
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.db.dao.DriveBackupDao
import com.ntg.vocabs.db.dao.TimeSpentDao
import com.ntg.vocabs.db.dao.VocabListDao
import com.ntg.vocabs.db.dao.WordDao
import com.ntg.vocabs.model.DriveBackup
import com.ntg.vocabs.model.GoogleDriveSate
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.model.req.BackupUserData
import com.ntg.vocabs.util.Constant.VOCAB_FOLDER_NAME_DRIVE
import com.ntg.vocabs.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val timeSpentDao: TimeSpentDao,
    private val vocabListDao: VocabListDao,
    private val driveBackupDao: DriveBackupDao,
    private val backupDao: DriveBackupDao
) : ViewModel() {

    private var drive: Drive? = null
    private var allBackups: LiveData<List<DriveBackup>> = MutableLiveData()
    var googleDriveState = MutableStateFlow<GoogleDriveSate?>(null)
    private var filLists: MutableLiveData<List<String>> = MutableLiveData()

    fun googleInstance(context: Context) {
        GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->

            // get credentials
            val credential = GoogleAccountCredential.usingOAuth2(
                context, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = googleAccount.account!!

            val transport: HttpTransport = NetHttpTransport()
            val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()


            // get Drive Instance
            drive = Drive
                .Builder(
                    transport,
                    jsonFactory,
                    credential
                )
                .setApplicationName(context.getString(R.string.app_name))
                .build()
        }
    }

    fun createFolder() {
        if (drive == null) return

        viewModelScope.launch(Dispatchers.IO) {
            if (!checkFolderExists()) {
                // Define a Folder
                val gFolder = com.google.api.services.drive.model.File()
                // Set file name and MIME
                gFolder.name = VOCAB_FOLDER_NAME_DRIVE
                gFolder.mimeType = "application/vnd.google-apps.folder"

                // You can also specify where to create the new Google folder
                // passing a parent Folder Id
                val parents: MutableList<String> = ArrayList(1)
//            parents.add("your_parent_folder_id_here")
                gFolder.parents = parents
                drive!!.Files().create(gFolder).setFields("id").execute()
                googleDriveState.value = GoogleDriveSate.FolderCreated
            } else {
                timber("Folder Already exist")
                googleDriveState.value = GoogleDriveSate.AlreadyExist
            }
        }

    }

    private fun checkFolderExists(): Boolean {
        return try {
            val query =
                "mimeType='application/vnd.google-apps.folder' and name='$VOCAB_FOLDER_NAME_DRIVE'"
            val result = drive!!.files().list().setQ(query).execute()
            result.files?.isNotEmpty() ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getFilesInFolder(): MutableLiveData<List<String>> {
        viewModelScope.launch(Dispatchers.IO) {
            val folderId = getFolderId()
            if (folderId.isNullOrEmpty()) {
                println("Folder not found.")
                filLists.postValue(emptyList())
            }

            val query = "'$folderId' in parents"
            val result = drive!!.files().list().setQ(query).execute()

            filLists.postValue(result.files?.map { it.name } ?: emptyList())
        }
        return filLists

    }

    private fun getFolderId(): String? {
        val query =
            "mimeType='application/vnd.google-apps.folder' and name='$VOCAB_FOLDER_NAME_DRIVE'"
        val result = drive!!.files().list().setQ(query).execute()

        return result.files?.firstOrNull()?.id
    }

    fun restoreBackup(context: Context, fileName: String, listener: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            val destinationPath =
                File(context.getExternalFilesDir(null)?.absolutePath, fileName).absolutePath
            val folderId = getFolderId()
            if (folderId.isNullOrEmpty()) {
                println("Folder not found.")
                listener.invoke(null)
                return@launch
            }

            val query = "'$folderId' in parents and name='$fileName'"
            val result = drive!!.files().list().setQ(query).execute()

            val file = result.files?.firstOrNull()
            if (file != null) {
                val outputStream = java.io.FileOutputStream(destinationPath)
                drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                outputStream.close()
                println("File '$fileName' downloaded successfully to '$destinationPath'")


                val json: String?
                val finalFile = File(context.getExternalFilesDir(""), fileName)

                finalFile.inputStream()
                json = try {
                    val inputStream: InputStream = finalFile.inputStream()
                    val size = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.read(buffer)
                    inputStream.close()
                    String(buffer, charset("UTF-8"))
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                listener.invoke(json)

//                val jsonObject = json?.let { JSONObject(it) }


            } else {
                listener.invoke(null)
                println("File '$fileName' not found in the folder.")
            }
        }
    }

    fun backupDB(file: File, listener:(Boolean) -> Unit ={}){
        viewModelScope.launch(Dispatchers.IO) {

            val folderId = getFolderId()
            if (folderId.isNullOrEmpty()) {
                return@launch
            }

            val gfile = com.google.api.services.drive.model.File()

            val fileContent = FileContent("application/octet-stream", file)
            gfile.name = "VocabsBackup_${System.currentTimeMillis()}"

            val parents: MutableList<String> = ArrayList(1)
            parents.add(folderId) // Here you need to get the parent folder id

            gfile.parents = parents

            try {
                drive!!.Files().create(gfile, fileContent).setFields("id").execute()
                driveBackupDao.insert(DriveBackup(0,true,System.currentTimeMillis().toString(),""))
                listener.invoke(true)
            }catch (e: Exception){
                driveBackupDao.insert(DriveBackup(0,false,System.currentTimeMillis().toString(),""))
                listener.invoke(false)
            }
        }
    }

    fun importToDB(content: String, callBack: (Boolean) -> Unit) {
        try {
            val backupUserData: BackupUserData =
                Gson().fromJson(content, BackupUserData::class.java)
            clearWordsTable()
            clearTimesTable()
            addAllWords(backupUserData.words ?: listOf())
            addAllTimeSpent(backupUserData.totalTimeSpent ?: listOf())
            addAllVocabLists(backupUserData.vocabList ?: listOf())
            callBack.invoke(true)

        } catch (e: Exception) {
            callBack.invoke(false)
            timber("restoreBackupError ::: ${e.message}")
        }
    }

    private fun clearWordsTable() {
        viewModelScope.launch {
            wordDao.clear()
        }
    }

    private fun clearTimesTable() {
        viewModelScope.launch {
            timeSpentDao.clear()
        }
    }

    private fun addAllWords(words: List<Word>) {
        viewModelScope.launch {
            wordDao.insertAll(words)
        }
    }


    private fun addAllTimeSpent(timeSpent: List<TimeSpent>) {
        viewModelScope.launch {
            timeSpentDao.insertAll(timeSpent)
        }
    }


    private fun addAllVocabLists(lists: List<VocabItemList>) {
        viewModelScope.launch {
            vocabListDao.insertAll(lists)
        }
    }

    fun backupLog(driveBackup: DriveBackup){
        viewModelScope.launch {
            driveBackupDao.insert(driveBackup)
        }
    }

    fun getAllBackups(): LiveData<List<DriveBackup>> {
        viewModelScope.launch {
            allBackups = driveBackupDao.all()
        }
        return allBackups
    }
}