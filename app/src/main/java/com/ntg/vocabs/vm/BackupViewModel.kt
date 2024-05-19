package com.ntg.vocabs.vm

import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.ntg.vocabs.R
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
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_TIMES
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.Constant.VOCAB_FOLDER_NAME_DRIVE
import com.ntg.vocabs.util.worker.IMAGES
import com.ntg.vocabs.util.worker.Voices
import com.ntg.vocabs.util.getCurrentDate
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.unzip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val timeSpentDao: TimeSpentDao,
    private val vocabListDao: VocabListDao,
    private val driveBackupDao: DriveBackupDao,
    private val backupDao: DriveBackupDao,
    private val storage: FirebaseStorage,
    private val mFirestore: FirebaseFirestore
) : ViewModel() {

    private var drive: Drive? = null
//    private val storage = Firebase.storage

    private var allBackups: LiveData<List<DriveBackup>> = MutableLiveData()
    var googleDriveState = MutableStateFlow<GoogleDriveSate?>(null)
    private var lastFileName: MutableLiveData<com.google.api.services.drive.model.File?> =
        MutableLiveData()

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

    fun getLastFile(): MutableLiveData<com.google.api.services.drive.model.File?> {
        viewModelScope.launch(Dispatchers.IO) {
            val folderId = getFolderId()
            if (folderId.isNullOrEmpty()) {
                println("Folder not found.")
                lastFileName.postValue(com.google.api.services.drive.model.File())
            }

            val query = "'$folderId' in parents"
            val result = drive!!.files().list().setQ(query).execute()


            if (result.files.isNotEmpty()) {
                lastFileName.postValue(result.files.first { it.name.startsWith("VocabsBackup_") && !it.trashed.orFalse() })
            } else {
                lastFileName.postValue(com.google.api.services.drive.model.File())
            }


        }
        return lastFileName

    }

    private suspend fun getFolderId(): String? {
        val query =
            "mimeType='application/vnd.google-apps.folder' and name='$VOCAB_FOLDER_NAME_DRIVE'"
        val result = drive!!.files().list().setQ(query).execute()

        return result.files?.firstOrNull()?.id
    }

    fun restoreBackup(
        context: Context,
        file: com.google.api.services.drive.model.File,
        listener: (String?) -> Unit
    ) {
        val fileName = "LsatBackup"
        viewModelScope.launch(Dispatchers.IO) {

            val destinationPath =
                File(context.getExternalFilesDir(null)?.absolutePath, fileName).absolutePath
//            val folderId = getFolderId()
//            if (folderId.isNullOrEmpty()) {
//                println("Folder not found.")
//                listener.invoke(null)
//                return@launch
//            }
//
//            val query = "'$folderId' in parents and name='$fileName'"
//            val result = drive!!.files().list().setQ(query).execute()

//            val file = result.files?.firstOrNull()
            val outputStream = FileOutputStream(destinationPath)
            drive!!.files().get(file.id).executeMediaAndDownloadTo(outputStream)
            outputStream.close()
            println("File '$fileName' downloaded successfully to '$destinationPath'")


            val json: String?
            val finalFile = File(context.getExternalFilesDir(""), fileName) // zip file
            unzip(finalFile.path, context.getExternalFilesDir("")?.path.orEmpty())

            val unZippedFile = File(context.getExternalFilesDir("backups"), "backup")
            unZippedFile.inputStream()
            json = try {
                val inputStream: InputStream = unZippedFile.inputStream()
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


        }
    }


    fun backupOnDrive(context: Context, listener: (Boolean) -> Unit = {}) {
        zipFolder(
            context.getExternalFilesDir("backups")?.path.toString(),
            context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}"
        ).also {
            backupDB(
                File(context.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}"),
                listener
            )
        }
    }

    private fun backupDB(file: File, listener: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {

            val folderId = getFolderId()
            if (folderId.isNullOrEmpty()) {
                return@launch
            }

            val gfile = com.google.api.services.drive.model.File()

            val fileContent = FileContent("application/octet-stream", file)
            val backupName = "VocabsBackup_${getCurrentDate()}"
            gfile.name = backupName

            val parents: MutableList<String> = ArrayList(1)
            parents.add(folderId) // Here you need to get the parent folder id

            gfile.parents = parents

            try {
                val executedFile =
                    drive!!.Files().create(gfile, fileContent).setFields("id").execute()
                driveBackupDao.insert(
                    DriveBackup(
                        0,
                        true,
                        System.currentTimeMillis().toString(),
                        ""
                    )
                )
                removeOldBackups(executedFile.id, folderId)
                listener.invoke(true)
            } catch (e: Exception) {
                driveBackupDao.insert(
                    DriveBackup(
                        0,
                        false,
                        System.currentTimeMillis().toString(),
                        ""
                    )
                )
                listener.invoke(false)
            }
        }
    }

    private fun removeOldBackups(backId: String, folderId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
//            val folderId = getFolderId()

            if (folderId != null) {
                // List files in the folder
                val result = drive!!.files().list()
                    .setQ("'$folderId' in parents")
                    .execute()

                val files = result.files

                for (file in files) {
                    val fileId = file.id

                    if (fileId != backId && fileId.startsWith("VocabsBackup_")) {
                        // Delete the file
                        drive!!.files().delete(file.id).execute()
                    }
                }
            }
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


    fun importToDB(content: String, callBack: (Boolean) -> Unit = {}) {
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

    fun backupLog(driveBackup: DriveBackup) {
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

    fun checkBackupAvailable(
        email: String,
        exist: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference.child(email)
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                exist.invoke(listResult.items.isNotEmpty())
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }

    }

    fun restoreVocabularies(email: String, onSuccess: (Boolean) -> Unit) {
        val db = mFirestore
        val wordsRef = db.collection(BACKUP_WORDS)
        val listRef = db.collection(BACKUP_LISTS)
        val timesRef = db.collection(BACKUP_TIMES)
        wordsRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val word = document.toObject(Word::class.java)
                    word.fid = document.id
                    word.synced = true
                    word.imageSynced = if (word.imageSynced != null) true else null
                    word.voiceSynced = if (word.voiceSynced != null) true else null
                    viewModelScope.launch {
                        wordDao.insert(word)
                    }

                }
                onSuccess.invoke(true)
            }
            .addOnFailureListener { exception ->
                onSuccess.invoke(false)
            }


        listRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val vocabList = document.toObject(VocabItemList::class.java)
                    vocabList.synced = true
                    vocabList.isSelected = false
                    viewModelScope.launch {
                        vocabListDao.insert(vocabList)
                    }
                }
                onSuccess.invoke(true)
            }
            .addOnFailureListener { exception ->
                onSuccess.invoke(false)
            }


        timesRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val vocabTime = document.toObject(TimeSpent::class.java)
                    vocabTime.synced = true
                    viewModelScope.launch {
                        timeSpentDao.insert(vocabTime)
                    }
                }
                onSuccess.invoke(true)
            }
            .addOnFailureListener { exception ->
//                onSuccess.invoke(false)
            }
    }

    fun restoreBackupFromServer(
        context: Context,
        email: String,
        onFailure: () -> Unit
    ) {
        val storageRef = storage.reference.child(email)


        storageRef
            .listAll()
            .addOnSuccessListener {
                it.items.forEach { backupZip ->
                    val islandRef = storageRef.child(backupZip.name)
                    val localFile = File.createTempFile(backupZip.name, "zip")

                    islandRef.getFile(localFile).addOnSuccessListener {
                        unzip(localFile.path, context.getExternalFilesDir("")?.path.orEmpty())
                        val imagesDirectory = File(context.getExternalFilesDir(""), IMAGES)
                        val voicesDirectory = File(context.getExternalFilesDir(""), Voices)
                        if (voicesDirectory.exists()){
                            voicesDirectory.listFiles()
                                ?.let { it1 -> copyFilesToFolder(context, it1.map { it.path }, "backups/sounds") }
                        }

                        if (imagesDirectory.exists()){
                            imagesDirectory.listFiles()
                                ?.let { it1 -> copyFilesToFolder(context, it1.map { it.path }, "backups/images") }
                        }
                        imagesDirectory.deleteRecursively()
                        voicesDirectory.deleteRecursively()
                        localFile.delete()


                    }.addOnFailureListener {
                        onFailure.invoke()
                    }


                }

            }.addOnFailureListener {
                onFailure.invoke()
            }


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
}