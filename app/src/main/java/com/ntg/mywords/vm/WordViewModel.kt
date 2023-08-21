package com.ntg.mywords.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.BuildConfig
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.response.WordData
import com.ntg.mywords.model.response.WordDataItem
import com.ntg.mywords.util.getUnixTimeNDaysAgo
import com.ntg.mywords.util.safeApiCall
import com.ntg.mywords.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val timeSpentDao: TimeSpentDao,
    private val api: ApiService
) : ViewModel() {

    private var isExist = false
    private var myWords: LiveData<List<Word>> = MutableLiveData()
    private var recentWordsCount: LiveData<Int> = MutableLiveData()
    private var word: LiveData<Word> = MutableLiveData()
    var searchedWord: MutableLiveData<List<Word>> = MutableLiveData()
    var searchedRecentWord: MutableLiveData<List<Word>> = MutableLiveData()
    private var allValidTimeSpent: LiveData<List<TimeSpent>> = MutableLiveData()
    private var wordData: MutableLiveData<NetworkResult<List<WordDataItem>>> = MutableLiveData()



    fun searchOnWords(query: String){
        viewModelScope.launch {
            searchedWord.value = wordDao.search(query)
        }
    }

    fun searchOnRecentWords(query: String){
        viewModelScope.launch {
            searchedRecentWord.value = wordDao.searchOnRecent(query, 7.getUnixTimeNDaysAgo(), System.currentTimeMillis())
        }
    }

    fun addNewWord(word: Word) {

        viewModelScope.launch {
            wordDao.insert(word)
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

    fun getMyWords():LiveData<List<Word>>  {
        viewModelScope.launch {
            myWords = wordDao.getAllWords()
        }
        return myWords

    }

    fun recentWords(daysAgo: Int) =wordDao.recentWordsCount(daysAgo.getUnixTimeNDaysAgo(), System.currentTimeMillis())

    fun findWord(id: Int?):LiveData<Word>?{
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
        return if (wordData.value?.data.orEmpty().isNotEmpty() && wordData.value?.data.orEmpty().first().entryMetadata?.id?.split(":")?.first().equals(word)){
            wordData
        }else{
            viewModelScope.launch {
                wordData = safeApiCall(Dispatchers.IO){
                    api.getDataWord(word)
                } as MutableLiveData<NetworkResult<List<WordDataItem>>>
            }
            wordData
        }

    }


}