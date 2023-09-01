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
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.db.dao.VocabListDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.di.DataRepository
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.req.BackupUserData
import com.ntg.mywords.model.response.ResponseBody
import com.ntg.mywords.model.response.WordDataItem
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
    private val vocabListDao: VocabListDao,
    private val api: DictionaryApiService,
    private val vocabApi: ApiService,
    private val dataRepository: DataRepository
) : ViewModel() {

    private var isExist = false
    private var myWords: LiveData<List<Word>> = MutableLiveData()
    private var recentWordsCount: LiveData<Int> = MutableLiveData()
    private var word: LiveData<Word> = MutableLiveData()
    var searchedWord: MutableLiveData<List<Word>> = MutableLiveData()
    var searchedRecentWord: MutableLiveData<List<Word>> = MutableLiveData()
    private var allValidTimeSpent: LiveData<List<TimeSpent>> = MutableLiveData()
    private var wordData: MutableLiveData<NetworkResult<List<WordDataItem>>> = MutableLiveData()
    private var uploadStatus: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var userBackup: MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>> =
        MutableLiveData()
    private lateinit var _recentLocations: Flow<UserDataAndSetting>


    fun searchOnWords(query: String) {
        viewModelScope.launch {
            searchedWord.value = wordDao.search(query)
        }
    }

    fun searchOnRecentWords(query: String) {
        viewModelScope.launch {
            searchedRecentWord.value =
                wordDao.searchOnRecent(query, 7.getUnixTimeNDaysAgo(), System.currentTimeMillis())
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

    fun getMyWords(): LiveData<List<Word>> {
        viewModelScope.launch {
            myWords = wordDao.getAllWords()
        }
        return myWords

    }

    fun recentWords(daysAgo: Int) =
        wordDao.recentWordsCount(daysAgo.getUnixTimeNDaysAgo(), System.currentTimeMillis())

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

    fun addAllTimeSpent(timeSpent: List<TimeSpent>) {
        viewModelScope.launch {
            timeSpentDao.insertAll(timeSpent)
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

    fun addNewVocabList(vocabItemList: VocabItemList) = viewModelScope.launch {
        vocabListDao.insert(vocabItemList)
    }

    fun selectList(id: Int) = viewModelScope.launch {
        vocabListDao.selectList(id)
    }

    fun getIdOfListSelected() = vocabListDao.getDataOfListSelected()

    fun isListExist(name: String, language: String) = vocabListDao.isExist(name, language)

    fun restoreUserBackup(email: String): MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>> {
        viewModelScope.launch {
            userBackup = safeApiCall(Dispatchers.IO) {
                vocabApi.restoreUserData(email = email, token = BuildConfig.VOCAB_API_KEY)
            } as MutableLiveData<NetworkResult<ResponseBody<BackupUserData>>>
        }
        return userBackup
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

}