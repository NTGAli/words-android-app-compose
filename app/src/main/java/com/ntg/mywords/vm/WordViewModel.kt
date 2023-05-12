package com.ntg.mywords.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.db.WordDao
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordDao: WordDao
) : ViewModel() {

    private var isExist = false

    fun addNewWord(word: Word) {

        viewModelScope.launch {
            wordDao.insert(word)
        }

    }

    fun checkIfExist(word: String, type: String): Boolean {
        viewModelScope.launch {
            isExist = wordDao.isExist(word, type)
        }
        timber("checkIfExist", isExist.toString())
        return isExist
    }


}