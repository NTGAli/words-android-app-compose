package com.ntg.mywords.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.mywords.db.dao.GermanNounsDao
import com.ntg.mywords.db.dao.GermanVerbsDao
import com.ntg.mywords.model.data.GermanDataVerb
import com.ntg.mywords.model.data.GermanPronouns
import com.ntg.mywords.model.db.GermanNouns
import com.ntg.mywords.model.db.GermanVerbs
import com.ntg.mywords.util.timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject


@AndroidEntryPoint
class ImportDbService : Service() {

    @Inject
    lateinit var germanNounsDao: GermanNounsDao

    @Inject
    lateinit var germanVerbsDao: GermanVerbsDao

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (checkIfFileExists("GAP.json")) {
            insertGermanNouns()
        }
        if (checkIfFileExists("verb.zip")) {
            insertVerbs()
        }

        return START_STICKY
    }

    private fun insertVerbs() {

        if (unzip("${getExternalFilesDir("")}/verb.zip", getExternalFilesDir("")?.path.orEmpty())) {
            val json: String?
            val file = File(getExternalFilesDir(""), "combined_data.json")

            file.inputStream()
            json = try {
                val inputStream: InputStream = file.inputStream()
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, charset("UTF-8"))
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }

            val jsonArray = JSONArray(json)
            val germanVerbList = mutableListOf<GermanVerbs>()

            try {
                for (i in 0 until jsonArray.length() - 1) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val word = jsonObject.getString("word")
                    val dataJson = jsonObject.getString("data")
                    val data = Gson().fromJson(dataJson, GermanDataVerb::class.java)
                    val verb = GermanVerbs(word = word, data = data)
                    germanVerbList.add(verb)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            CoroutineScope(Dispatchers.IO).launch {
                germanVerbsDao.insertAll(germanVerbList)
                file.delete()
                stopSelf()
            }
        }
    }

    private fun unzip(zipFilePath: String, destinationDir: String): Boolean {
        val buffer = ByteArray(1024)

        try {
            // Create input stream from the zip file
            val zipInputStream = ZipInputStream(FileInputStream(zipFilePath))

            // Create the destination directory if it doesn't exist
            val destDir = File(destinationDir)
            if (!destDir.exists()) {
                destDir.mkdir()
            }

            // Iterate through each entry in the zip file
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val newFile = File(destinationDir + File.separator + zipEntry.name)

                // Create parent directories if they don't exist
                newFile.parent?.let { File(it).mkdirs() }

                // Write the current entry to the destination file
                val fos = newFile.outputStream()
                var len: Int
                while (zipInputStream.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()

                // Move to the next entry
                zipEntry = zipInputStream.nextEntry
            }

            // Close the zip input stream
            zipInputStream.closeEntry()
            zipInputStream.close()
            File(zipFilePath).delete()

            println("Unzip completed successfully.")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun checkIfFileExists(fileName: String): Boolean {
        val file = File(getExternalFilesDir(""), fileName)
        return file.exists()
    }


    private fun insertGermanNouns(): MutableList<GermanNouns>? {
        val json: String?
        val file = File(getExternalFilesDir(""), "GAP.json")

        file.inputStream()
        json = try {
            val inputStream: InputStream = file.inputStream()
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        val jsonArray = JSONArray(json)
        val bookList = mutableListOf<GermanNouns>()

        try {
            for (i in 0 until jsonArray.length() - 1) {
                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.getString("lemma")
                val genus = jsonObject.getString("genus")
                val plural = jsonObject.getString("plural")
                val book = GermanNouns(lemma = title, genus = genus, plural = plural)
                bookList.add(book)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO).launch {
            germanNounsDao.insertAll(bookList)
            file.delete()
            stopSelf()
        }

        return bookList
    }
}