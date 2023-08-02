package com.ntg.mywords.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.db.WordDao
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordDao: WordDao
) : ViewModel() {

    private var isExist = false
    private var myWords: LiveData<List<Word>> = MutableLiveData()
    private var word: LiveData<Word> = MutableLiveData()



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

    fun getMyWords():LiveData<List<Word>>  {
        viewModelScope.launch {
            myWords = wordDao.getAllWords()
        }
        return myWords

    }

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


}