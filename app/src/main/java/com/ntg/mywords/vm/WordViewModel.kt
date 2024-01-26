package com.ntg.mywords.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ntg.mywords.BuildConfig
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.DictionaryApiService
import com.ntg.mywords.api.FreeDictionaryApi
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.db.dao.GermanNounsDao
import com.ntg.mywords.db.dao.GermanVerbsDao
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.db.dao.VocabListDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.di.DataRepository
import com.ntg.mywords.model.db.GermanNouns
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.req.BackupUserData
import com.ntg.mywords.model.response.DictionaryResItem
import com.ntg.mywords.model.response.ResponseBody
import com.ntg.mywords.model.response.WordDataItem
import com.ntg.mywords.model.response.WordVocab
import com.ntg.mywords.util.getUnixTimeNDaysAgo
import com.ntg.mywords.util.safeApiCall
import com.ntg.mywords.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val timeSpentDao: TimeSpentDao,
    private val germanNounsDao: GermanNounsDao,
    private val germanVerbsDao: GermanVerbsDao,
    private val vocabListDao: VocabListDao,
    private val api: DictionaryApiService,
    private val freeApiDic: FreeDictionaryApi,
    private val vocabApi: ApiService,
    private val dataRepository: DataRepository
) : ViewModel() {

    private var isExist = false
    private var myWords: LiveData<List<Word>> = MutableLiveData()
    private var allWords: LiveData<List<Word>> = MutableLiveData()
    private var recentWordsCount: LiveData<Int> = MutableLiveData()
    private var word: LiveData<Word> = MutableLiveData()
    private var dataList: LiveData<VocabItemList> = MutableLiveData()
    private var germanNounSize: LiveData<Int> = MutableLiveData()
    private var germanVerbsSize: LiveData<Int> = MutableLiveData()
    private var germanNoun: LiveData<GermanNouns> = MutableLiveData()
    var searchedWord: MutableLiveData<List<Word>> = MutableLiveData()
    var searchedWordOnBookmarked: MutableLiveData<List<Word>> = MutableLiveData()
    var searchedRecentWord: MutableLiveData<List<Word>> = MutableLiveData()
    private var allValidTimeSpentBaseListId: LiveData<List<TimeSpent>> = MutableLiveData()
    private var allValidTimeSpent: LiveData<List<TimeSpent>> = MutableLiveData()
    private var wordData: MutableLiveData<NetworkResult<List<WordDataItem>>> = MutableLiveData()
    private var wordVocab: MutableLiveData<NetworkResult<ResponseBody<WordVocab?>>> = MutableLiveData()
    private var wordFreeDic: MutableLiveData<NetworkResult<List<DictionaryResItem>?>> = MutableLiveData()
    private var uploadStatus: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var userBackup: MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>> =
        MutableLiveData()
    private var lastUserBackupDate: MutableLiveData<NetworkResult<String>> =
        MutableLiveData()
    private lateinit var _recentLocations: Flow<UserDataAndSetting>


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

    fun insertAllGermanNouns(list: List<GermanNouns>){
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

    fun germanNoun(word: String): LiveData<GermanNouns> {
        viewModelScope.launch {
            germanNoun = germanNounsDao.findNoun(word)
        }
        return germanNoun
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

    fun recentWords(daysAgo: Int, listId: Int) =
        wordDao.recentWordsCount(daysAgo.getUnixTimeNDaysAgo(), System.currentTimeMillis(), listId)

    fun findWord(id: Int?): LiveData<Word>? {
        if (id == -1) return null
        viewModelScope.launch {
            word = wordDao.findWord(id)
        }
        return word

    }

    fun checkIfExist(word: String, type: String): Boolean {
        viewModelScope.launch {
            isExist = wordDao.isExist(word, type)
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


    fun getWord(word: String, type: String): MutableLiveData<NetworkResult<ResponseBody<WordVocab?>>> {
        viewModelScope.launch {
            wordVocab = safeApiCall(Dispatchers.IO){
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

    fun addNewVocabList(vocabItemList: VocabItemList) = viewModelScope.launch {
        vocabListDao.insert(vocabItemList)
    }

    fun updateVocabList(vocabItemList: VocabItemList) = viewModelScope.launch {
        vocabListDao.update(vocabItemList)
    }

    fun deleteListById(id: Int) {
        viewModelScope.launch {
            vocabListDao.deleteById(id)
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
            addAllWords(backupUserData.words ?: listOf())
            addAllTimeSpent(backupUserData.totalTimeSpent ?: listOf())
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

}