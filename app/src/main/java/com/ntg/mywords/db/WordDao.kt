package com.ntg.mywords.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.mywords.model.db.Word

@Dao
interface WordDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(word: Word)

    @Query("SELECT * FROM Word")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT EXISTS(SELECT * FROM Word WHERE word =:word AND type =:type)")
    suspend fun isExist(word: String, type: String): Boolean

}