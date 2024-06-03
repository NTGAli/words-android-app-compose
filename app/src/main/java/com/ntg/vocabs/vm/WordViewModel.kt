package com.ntg.vocabs.vm

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ntg.vocabs.BuildConfig
import com.ntg.vocabs.UserDataAndSetting
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.api.DictionaryApiService
import com.ntg.vocabs.api.FreeDictionaryApi
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.db.dao.EnglishVerbDao
import com.ntg.vocabs.db.dao.EnglishWordDao
import com.ntg.vocabs.db.dao.GermanNounsDao
import com.ntg.vocabs.db.dao.GermanVerbsDao
import com.ntg.vocabs.db.dao.SoundDao
import com.ntg.vocabs.db.dao.TimeSpentDao
import com.ntg.vocabs.db.dao.VocabListDao
import com.ntg.vocabs.db.dao.WordDao
import com.ntg.vocabs.di.DataRepository
import com.ntg.vocabs.model.data.GermanDataVerb
import com.ntg.vocabs.model.db.EnglishVerbs
import com.ntg.vocabs.model.db.EnglishWords
import com.ntg.vocabs.model.db.GermanNouns
import com.ntg.vocabs.model.db.GermanVerbs
import com.ntg.vocabs.model.db.Sounds
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.model.req.BackupUserData
import com.ntg.vocabs.model.response.DictionaryResItem
import com.ntg.vocabs.model.response.ResponseBody
import com.ntg.vocabs.model.response.WordDataItem
import com.ntg.vocabs.model.response.WordVocab
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_TIMES
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.getUnixTimeNDaysAgo
import com.ntg.vocabs.util.safeApiCall
import com.ntg.vocabs.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val timeSpentDao: TimeSpentDao,
    private val germanNounsDao: GermanNounsDao,
    private val germanVerbsDao: GermanVerbsDao,
    private val soundDao: SoundDao,
    private val englishVerbDao: EnglishVerbDao,
    private val vocabListDao: VocabListDao,
    private val englishWordDao: EnglishWordDao,
    private val api: DictionaryApiService,
    private val freeApiDic: FreeDictionaryApi,
    private val vocabApi: ApiService,
    private val dataRepository: DataRepository
) : ViewModel() {

    private var isExist = false
    private var myWords: LiveData<List<Word>> = MutableLiveData()
    private var allWords: LiveData<List<Word>> = MutableLiveData()
    private var sizeOfAllWords: LiveData<Int> = MutableLiveData()
    private var recentWordsCount: LiveData<Int> = MutableLiveData()
    private var word: LiveData<Word> = MutableLiveData()
    private var words: LiveData<List<Word>> = MutableLiveData()
    private var wordsWirhDef: LiveData<List<Word>?> = MutableLiveData()
    private var dataList: LiveData<VocabItemList> = MutableLiveData()
    private var germanNounSize: LiveData<Int> = MutableLiveData()
    private var germanVerbsSize: LiveData<Int> = MutableLiveData()
    private var soundsSize: LiveData<Int> = MutableLiveData()
    private var germanNoun: LiveData<GermanNouns> = MutableLiveData()
    private var germanVerb: LiveData<GermanVerbs> = MutableLiveData()
    private var germanVerbs: LiveData<List<GermanVerbs>> = MutableLiveData()
    private var englishVerb: LiveData<EnglishVerbs> = MutableLiveData()
    var searchedWord: MutableLiveData<List<Word>> = MutableLiveData()
    var searchedWordOnBookmarked: MutableLiveData<List<Word>> = MutableLiveData()
    var searchedRecentWord: MutableLiveData<List<Word>> = MutableLiveData()
    private var allValidTimeSpentBaseListId: LiveData<List<TimeSpent>> = MutableLiveData()
    private var allValidTimeSpent: LiveData<List<TimeSpent>> = MutableLiveData()
    private var englishWordsSize: LiveData<Int> = MutableLiveData()
    private var englishVerbsSize: LiveData<Int> = MutableLiveData()
    private var pronouns: LiveData<Sounds> = MutableLiveData()
    private var englishWords: LiveData<List<EnglishWords>> = MutableLiveData()
    private var wordData: MutableLiveData<NetworkResult<List<WordDataItem>>> = MutableLiveData()
    private var wordVocab: MutableLiveData<NetworkResult<ResponseBody<WordVocab?>>> =
        MutableLiveData()
    private var wordFreeDic: MutableLiveData<NetworkResult<List<DictionaryResItem>?>> =
        MutableLiveData()
    private var uploadStatus: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var userBackup: MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>> =
        MutableLiveData()
    private var lastUserBackupDate: MutableLiveData<NetworkResult<String>> =
        MutableLiveData()
    private lateinit var _recentLocations: Flow<UserDataAndSetting>
    private var processingEnWords = false
    private var processingEnVerbs = false
    private var processingNouns = false
    private var processingArticles = false
    var scrollPos: Int = 0

    fun searchOnWords(query: String, listId: Int) {
        viewModelScope.launch {
            searchedWord.value = wordDao.search(query, listId)
        }
    }

    fun searchOnRecentWords(query: String, listId: Int) {
        viewModelScope.launch {
            searchedRecentWord.value =
                wordDao.searchOnRecent(
                    query,
                    7.getUnixTimeNDaysAgo(),
                    System.currentTimeMillis(),
                    listId
                )
        }
    }

    fun searchOnBookmarked(query: String, listId: Int) {
        viewModelScope.launch {
            searchedWordOnBookmarked.value = wordDao.searchOnBookmark(query, listId)
        }
    }

    fun addNewWord(word: Word) {
        viewModelScope.launch {
            wordDao.insert(word)
        }
    }

    fun addAllWords(words: List<Word>) {
        viewModelScope.launch {
            wordDao.insertAll(words)
        }
    }

    fun editWord(id: Int?, word: Word) {
        viewModelScope.launch {
            wordDao.update(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            wordDao.delete(word)
        }
    }

    fun deleteWord(id: Int) {
        viewModelScope.launch {
            wordDao.delete(id)
        }
    }

    fun deleteWordsOfList(listId: Int) {
        viewModelScope.launch {
            wordDao.deleteWordOfList(listId)
        }
    }


    fun clearWordsTable() {
        viewModelScope.launch {
            wordDao.clear()
        }
    }

    fun clearTimesTable() {
        viewModelScope.launch {
            timeSpentDao.clear()
        }
    }

    fun clearVocabListsTable() {
        viewModelScope.launch {
            vocabListDao.clear()
        }
    }

    fun insertAllGermanNouns(list: List<GermanNouns>) {
        viewModelScope.launch {
            germanNounsDao.insertAll(list)
        }
    }

    fun sizeGermanNoun(): LiveData<Int> {
        viewModelScope.launch {
            germanNounSize = germanNounsDao.size()
        }
        return germanNounSize
    }

    fun sizeGermanVerbs(): LiveData<Int> {
        viewModelScope.launch {
            germanVerbsSize = germanVerbsDao.size()
        }
        return germanVerbsSize
    }

    fun sizeSounds(): LiveData<Int> {
        viewModelScope.launch {
            germanVerbsSize = soundDao.size()
        }
        return soundsSize
    }

    fun germanNoun(word: String): LiveData<GermanNouns> {
        viewModelScope.launch {
            germanNoun = germanNounsDao.findNoun(word)
        }
        return germanNoun
    }

    fun germanVerb(word: String): LiveData<GermanVerbs> {
        viewModelScope.launch {
            germanVerb = germanVerbsDao.findVerb(word)
        }
        return germanVerb
    }


    fun allGermanVerbs(query: String): LiveData<List<GermanVerbs>> {
        viewModelScope.launch {
            germanVerbs = germanVerbsDao.getAllVerbs(query)
        }
        return germanVerbs
    }

    fun englishVerb(word: String): LiveData<EnglishVerbs> {
        viewModelScope.launch {
            englishVerb = englishVerbDao.find(word)
        }
        return englishVerb
    }

    fun getWordsBaseListId(listId: Int): LiveData<List<Word>> {
        viewModelScope.launch {
            myWords = wordDao.getWordBaseListId(listId)
        }
        return myWords
    }

    fun getAllWords(): LiveData<List<Word>> {
        viewModelScope.launch {
            allWords = wordDao.getAllWords()
        }
        return allWords
    }

    fun getSizeOfWords(): LiveData<Int> {
        viewModelScope.launch {
            sizeOfAllWords = wordDao.size()
        }
        return sizeOfAllWords
    }

    fun recentWords(daysAgo: Int, listId: Int) =
        wordDao.recentWordsCount(daysAgo.getUnixTimeNDaysAgo(), System.currentTimeMillis(), listId)

    fun randomWords(listId: Int) =
        wordDao.randomWords(listId)

    fun findWord(id: Int?): LiveData<Word>? {
        if (id == -1) return null
        viewModelScope.launch {
            word = wordDao.findWord(id)
        }
        return word
    }

    fun findWord(w: String, type: String): LiveData<List<Word>>? {
        if (w.isEmpty() || type.isEmpty()) return null
        if (w.isEmpty()) return null
        viewModelScope.launch {
            words = wordDao.findWord(w, type)
        }
        return words
    }

    fun findWordWithDef(w: String, type: String, def: String): LiveData<List<Word>?>? {
        if (w.isEmpty() || type.isEmpty()) return null
        if (w.isEmpty()) return null
        viewModelScope.launch {
            wordsWirhDef = wordDao.findWordWithDef(w, type, def)
        }
        return wordsWirhDef
    }

    fun checkIfExist(word: String, type: String, def: String): Boolean {
        viewModelScope.launch {
            isExist = wordDao.isExist(word, type, def)
        }
        timber("checkIfExist", isExist.toString())
        return isExist
    }

    fun getAllValidTimeSpentBaseListId(listId: Int): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            allValidTimeSpentBaseListId = timeSpentDao.getAllValidTimeBaseListId(listId)
        }
        return allValidTimeSpentBaseListId
    }

    fun getAllValidTimeSpent(): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            allValidTimeSpent = timeSpentDao.getAllValidTime()
        }
        return allValidTimeSpent
    }

    fun getEnglishWordsSize(): LiveData<Int> {
        viewModelScope.launch {
            englishWordsSize = englishWordDao.size()
        }
        return englishWordsSize
    }

    fun getEnglishVerbsSize(): LiveData<Int> {
        viewModelScope.launch {
            englishVerbsSize = englishVerbDao.size()
        }
        return englishVerbsSize
    }

    fun findPronouns(word: String, type: String): LiveData<Sounds> {
        viewModelScope.launch {
            pronouns = soundDao.search(word, type)
        }
        return pronouns
    }

    fun englishWords(query: String): Flow<PagingData<EnglishWords>> {
        return Pager(
            config = PagingConfig(pageSize = 100),
            pagingSourceFactory = {
                englishWordDao.search(query)
            }
        ).flow
            .cachedIn(viewModelScope)
    }

    fun getDataWord(word: String): MutableLiveData<NetworkResult<List<WordDataItem>>> {
        return if (wordData.value?.data.orEmpty().isNotEmpty() && wordData.value?.data.orEmpty()
                .first().entryMetadata?.id?.split(":")?.first().equals(word)
        ) {
            wordData
        } else {
            viewModelScope.launch {
                wordData = safeApiCall(Dispatchers.IO) {
                    api.getDataWord(word)
                } as MutableLiveData<NetworkResult<List<WordDataItem>>>
            }
            wordData
        }

    }

    fun getDataWordFromFreeDictionary(word: String): MutableLiveData<NetworkResult<List<DictionaryResItem>?>> {
        viewModelScope.launch {
            wordFreeDic = safeApiCall(Dispatchers.IO) {
                freeApiDic.getDataWord(word)
            } as MutableLiveData<NetworkResult<List<DictionaryResItem>?>>
        }
        return wordFreeDic
    }


    fun getWord(
        word: String,
        type: String
    ): MutableLiveData<NetworkResult<ResponseBody<WordVocab?>>> {
        viewModelScope.launch {
            wordVocab = safeApiCall(Dispatchers.IO) {
                vocabApi.getWord(word, type)
            } as MutableLiveData<NetworkResult<ResponseBody<WordVocab?>>>
        }
        return wordVocab
    }

    fun addAllTimeSpent(timeSpent: List<TimeSpent>) {
        viewModelScope.launch {
            timeSpentDao.insertAll(timeSpent)
        }
    }


    fun deleteTimeSpentOfList(listId: Int) {
        viewModelScope.launch {
            timeSpentDao.deleteTimeOfList(listId)
        }
    }

    fun addAllVocabLists(lists: List<VocabItemList>) {
        viewModelScope.launch {
            vocabListDao.insertAll(lists)
        }
    }

    fun upload(filePart: MultipartBody.Part): MutableLiveData<NetworkResult<String>> {

        viewModelScope.launch {
            uploadStatus = safeApiCall(Dispatchers.IO) {
                vocabApi.uploadFile(filePart)
            } as MutableLiveData<NetworkResult<String>>
        }

        return uploadStatus

    }


    fun upload(userData: BackupUserData, email: String): MutableLiveData<NetworkResult<String>> {

        viewModelScope.launch {
            uploadStatus = safeApiCall(Dispatchers.IO) {
                vocabApi.backupUserData(
                    backupUserData = userData,
                    email = email,
                    token = BuildConfig.VOCAB_API_KEY,
                    version = BuildConfig.VERSION_NAME,
                )
            } as MutableLiveData<NetworkResult<String>>
        }

        return uploadStatus

    }

    fun getAllVocabList() = vocabListDao.getAllVocabList()

    fun getListWithCount() = vocabListDao.getListWithNumberOfWords()

    fun checkIfNoListSelected() =
        viewModelScope.launch { vocabListDao.updateFirstItemIfAllNotSelected() }

    fun addNewVocabList(vocabItemList: VocabItemList, id:(Long) -> Unit) = viewModelScope.launch {
        vocabListDao.insert(vocabItemList).let {
            id.invoke(it)
        }
    }

    fun updateVocabList(vocabItemList: VocabItemList) = viewModelScope.launch {
        vocabListDao.update(vocabItemList)
    }

    fun deleteListById(id: Int) {
        viewModelScope.launch {
            vocabListDao.deleteById(id)
        }
    }

    fun deleteList(vocabItemList: VocabItemList) {
        viewModelScope.launch {
            vocabListDao.delete(vocabItemList)
        }
    }

    fun selectList(id: Int) = viewModelScope.launch {
        vocabListDao.selectList(id)
    }

    fun findList(id: Int?): LiveData<VocabItemList>? {
        if (id == -1) return null
        viewModelScope.launch {
            dataList = vocabListDao.findList(id)
        }
        return dataList

    }

    fun currentList() = vocabListDao.getDataOfListSelected()

    fun isListExist(name: String, language: String) = vocabListDao.isExist(name, language)

    fun restoreUserBackup(email: String): MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>> {
        viewModelScope.launch {
            userBackup = safeApiCall(Dispatchers.IO) {
                vocabApi.restoreUserData(email = email, token = BuildConfig.VOCAB_API_KEY)
            } as MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>>
        }
        return userBackup
    }


    fun lastUserBackup(email: String): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            lastUserBackupDate = safeApiCall(Dispatchers.IO) {
                vocabApi.lastUserBackup(email = email, token = BuildConfig.VOCAB_API_KEY)
            } as MutableLiveData<NetworkResult<String>>
        }
        return lastUserBackupDate
    }

    fun importToDB(content: String, callBack: (Boolean) -> Unit) {
        try {
            val backupUserData: BackupUserData =
                Gson().fromJson(content, BackupUserData::class.java)
            clearWordsTable()
            clearTimesTable()
            addAllWords(backupUserData.words.orEmpty())
            addAllTimeSpent(backupUserData.totalTimeSpent.orEmpty())
            addAllVocabLists(backupUserData.vocabList.orEmpty())
            callBack.invoke(true)

        } catch (e: Exception) {
            callBack.invoke(false)
            timber("restoreBackupError ::: ${e.message}")
        }
    }


    fun getUserData(): Flow<UserDataAndSetting> {
        viewModelScope.launch {
            _recentLocations = dataRepository.getUserData()
        }
        return _recentLocations
    }

    fun isBookmarked(isBookmarked: Boolean, id: Int) =
        viewModelScope.launch { wordDao.isBookmark(isBookmarked, id) }


    fun unzipRaw(inputStream: InputStream) {
        timber("ENGLISH_WORD ::: START")
        viewModelScope.launch(Dispatchers.IO) {
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

                    if (zipEntry.name == "en_words.json" || zipEntry.name == "articles.json" || zipEntry.name == "combined_data.json") {
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
    }

    private suspend fun processJsonArray(jsonArray: JSONArray, name: String) {

        when(name){

            "en_words.json" -> {
                if (processingEnWords) return
                processingEnWords = true
                val list = arrayListOf<EnglishWords>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val word = jsonObject.getString("word")
                    val type = jsonObject.getString("type")
                    list.add(EnglishWords(0, word, type))
                }
                englishWordDao.insertAll(list)
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
                germanNounsDao.insertAll(germanNouns)
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

                germanVerbsDao.insertAll(germanVerbList)
            }

        }

    }

    fun insertEnglishVerbsFromRes(openRawResource: InputStream) {
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

    private fun insertEnglishVerbs(jsonArray: JSONArray) {
        val verbs = arrayListOf<EnglishVerbs>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val word = jsonObject.getString("word")
            val past = jsonObject.getString("pastSimple")
            val pp = jsonObject.getString("pp")
            val ing = jsonObject.getString("ing")
            verbs.add(EnglishVerbs(0, word = word, pastSimple = past, pp = pp, ing = ing))
        }
        viewModelScope.launch {
            englishVerbDao.insertAll(verbs)
        }
        timber("ENGLISH_WORD ::: END")
    }


    private fun setDataToNew(
        id: String,
        data: Any,
        type: String,
        email: String,
        isSuccess: (Any) -> Unit
    ) {
        val firestore = Firebase.firestore

        val docRef = firestore
            .collection(BuildConfig.VOCAB_PATH_DB).document(email)
            .collection(type).document(id).set(data)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    isSuccess.invoke(data)
                }
            }
    }

    fun transferOldToNew(
        email: String
    ){
        val db = Firebase.firestore
        val wordsRef = db.collection(Constant.BackTypes.BACKUP_TIMES)
        wordsRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    viewModelScope.launch {
                        setDataToNew(document.id, document.toObject(TimeSpent::class.java), BACKUP_TIMES, email){
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
            }
    }
}