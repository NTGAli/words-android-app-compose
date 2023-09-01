package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ntg.mywords.model.db.VocabItemList

@Dao
interface VocabListDao {

    @Insert
    suspend fun insert(vocabList: VocabItemList)

    @Query("SELECT * FROM VocabItemList")
    fun getAllVocabList(): LiveData<List<VocabItemList>>

}