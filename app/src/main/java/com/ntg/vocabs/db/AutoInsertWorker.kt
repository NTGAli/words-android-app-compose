package com.ntg.vocabs.db

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ntg.vocabs.R
import com.ntg.vocabs.model.data.GermanDataVerb
import com.ntg.vocabs.model.db.EnglishVerbs
import com.ntg.vocabs.model.db.EnglishWords
import com.ntg.vocabs.model.db.GermanNouns
import com.ntg.vocabs.model.db.GermanVerbs
import com.ntg.vocabs.model.db.Sounds
import com.ntg.vocabs.util.orTrue
import com.ntg.vocabs.util.timber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@HiltWorker
class AutoInsertWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDB: AppDB
) : CoroutineWorker(appContext, workerParams) {

    private var processingEnWords = false
    private var processingEnVerbs = false
    private var processingNouns = false
    private var processingSounds = false
    private var processingArticles = false

    override suspend fun doWork(): Result {
        unzipRaw(appContext.resources.openRawResource(R.raw.en_words))
        unzipRaw(appContext.resources.openRawResource(R.raw.articles))
        unzipRaw(appContext.resources.openRawResource(R.raw.combined_data))
        unzipRaw(appContext.resources.openRawResource(R.raw.pron))
        insertEnglishVerbsFromRes(appContext.resources.openRawResource(R.raw.word_forms))
        return Result.success()
    }

    private suspend fun unzipRaw(inputStream: InputStream) {
        try {
            val zipInputStream = ZipInputStream(inputStream)
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {

                val jsonStringBuilder = StringBuilder()
                val bufferedReader =
                    BufferedReader(InputStreamReader(zipInputStream, "UTF-8"))

                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    jsonStringBuilder.append(line)
                }

                bufferedReader.close()

                val jsonArray = JSONArray(jsonStringBuilder.toString())

                if (zipEntry.name == "en_words.json" || zipEntry.name == "articles.json" || zipEntry.name == "combined_data.json"
                    || zipEntry.name == "pron.json") {
                    processJsonArray(jsonArray, zipEntry.name)
                    break
                }

                zipEntry = zipInputStream.nextEntry
            }

            // Close the zip input stream
            zipInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private suspend fun processJsonArray(jsonArray: JSONArray, name: String) {
        timber("processingSounds :::::::::::::::: $name")
        when(name){

            "en_words.json" -> {
                if (processingEnWords) return
                processingEnWords = true
                val list = arrayListOf<EnglishWords>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val word = jsonObject.getString("word")
                    val type = jsonObject.getString("type")
                    if (word.firstOrNull()?.isLowerCase().orTrue()){
                        list.add(EnglishWords(0, word, type))
                    }
                }
                appDB.getEnglishWordsDao().insertAll(list)
                timber("ENGLISH_WORD ::: END")
            }

            "articles.json" -> {
                if (processingArticles) return
                processingArticles = true
                val germanNouns = mutableListOf<GermanNouns>()
                try {
                    for (i in 0 until jsonArray.length() - 1) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val title = jsonObject.getString("lemma")
                        val genus = jsonObject.getString("genus")
                        val plural = jsonObject.getString("plural")
                        val book = GermanNouns(lemma = title, genus = genus, plural = plural)
                        germanNouns.add(book)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                appDB.germanNounsDao().insertAll(germanNouns)
            }

            "combined_data.json" -> {
                if (processingNouns) return
                processingNouns = true
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

                appDB.germanVerbsDao().insertAll(germanVerbList)
            }

            "pron.json" -> {
                if (processingSounds) return
                processingSounds = true
                val sounds = mutableListOf<Sounds>()
                try {
                    for (i in 0 until jsonArray.length() - 1) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val word = jsonObject.getString("word")
                        val type = jsonObject.getString("type")
                        val mp3 = jsonObject.getString("mp3")
                        val pronouns = jsonObject.getString("pronunciation")
                        val sound = Sounds(word = word, type = type, mp3 = mp3, pronunciation = pronouns)
                        sounds.add(sound)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                appDB.getSoundsDao().insertAll(sounds)
            }

        }

    }

    private suspend fun insertEnglishVerbsFromRes(openRawResource: InputStream) {
        if (processingEnVerbs) return
        processingEnVerbs = true
        try {
            val jsonStringBuilder = StringBuilder()
            val bufferedReader =
                BufferedReader(InputStreamReader(openRawResource, "UTF-8"))

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                jsonStringBuilder.append(line)
            }

            bufferedReader.close()

            val jsonArray = JSONArray(jsonStringBuilder.toString())
            insertEnglishVerbs(jsonArray)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun insertEnglishVerbs(jsonArray: JSONArray) {
        val verbs = arrayListOf<EnglishVerbs>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val word = jsonObject.getString("word")
            val past = jsonObject.getString("pastSimple")
            val pp = jsonObject.getString("pp")
            val ing = jsonObject.getString("ing")
            verbs.add(EnglishVerbs(0, word = word, pastSimple = past, pp = pp, ing = ing))
        }
            appDB.getEnglishVerbsDao().insertAll(verbs)
        timber("ENGLISH_WORD ::: END")
    }


}