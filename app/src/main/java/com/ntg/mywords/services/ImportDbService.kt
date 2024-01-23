package com.ntg.mywords.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ntg.mywords.db.dao.GermanNounsDao
import com.ntg.mywords.model.db.GermanNouns
import com.ntg.mywords.util.timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


@AndroidEntryPoint
class ImportDbService: Service() {

    @Inject
    lateinit var germanNounsDao: GermanNounsDao

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (checkIfFileExists("GAP.json")){
            insertGermanNouns()
        }else if (checkIfFileExists("files/test.json")){
        }else if (checkIfFileExists("/files/test.json")){
        }

        return START_STICKY
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