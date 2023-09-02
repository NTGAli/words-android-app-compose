package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.mywords.model.db.Word

@Dao
interface WordDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<Word>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("DELETE FROM Word")
    suspend fun clear()

    @Query("SELECT * FROM Word ORDER BY id DESC")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE listId=:listId ORDER BY id DESC")
    fun getWordBaseListId(listId: Int): LiveData<List<Word>>


    @Query("SELECT * FROM Word WHERE id =:id")
    fun findWord(id: Int?): LiveData<Word>

    @Query("SELECT EXISTS(SELECT * FROM Word WHERE word =:word AND type =:type)")
    suspend fun isExist(word: String, type: String): Boolean

    @Query("SELECT COUNT(*) FROM Word WHERE (dateCreated BETWEEN :start AND :end) AND listId=:listId")
    fun recentWordsCount(start: Long, end: Long, listId: Int): LiveData<Int>

    @Query("SELECT * FROM Word WHERE (word LIKE '%' || :query || '%') AND listId=:listId")
    suspend fun search(query: String,listId: Int): List<Word>

    @Query("SELECT * FROM Word WHERE word LIKE '%' || :query || '%' AND (dateCreated BETWEEN :start AND :end) AND listId=:listId")
    suspend fun searchOnRecent(query: String, start: Long, end: Long, listId: Int): List<Word>
}