package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.vocabs.model.db.Word

@Dao
interface WordDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Upsert
    suspend fun insertAll(words: List<Word>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("DELETE FROM Word WHERE  listId =:listId")
    suspend fun deleteWordOfList(listId: Int)

    @Query("DELETE FROM Word")
    suspend fun clear()

    @Query("SELECT * FROM Word ORDER BY id DESC")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE listId=:listId ORDER BY id DESC")
    fun getWordBaseListId(listId: Int): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE id =:id")
    fun findWord(id: Int?): LiveData<Word>

    @Query("SELECT * FROM Word WHERE word =:word AND type = :type")
    fun findWord(word: String, type: String): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE word =:word AND type = :type AND definition = :def")
    fun findWordWithDef(word: String, type: String, def: String): LiveData<List<Word>?>

    @Query("SELECT EXISTS(SELECT * FROM Word WHERE word =:word AND type =:type AND definition =:def)")
    suspend fun isExist(word: String, type: String, def: String): Boolean

    @Query("SELECT COUNT(*) FROM Word WHERE (dateCreated BETWEEN :start AND :end) AND listId=:listId")
    fun recentWordsCount(start: Long, end: Long, listId: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM Word")
    fun size(): LiveData<Int>

    @Query("SELECT * FROM Word WHERE (word LIKE '%' || :query || '%') AND listId=:listId")
    suspend fun search(query: String,listId: Int): List<Word>

    @Query("SELECT * FROM Word WHERE (word LIKE '%' || :query || '%') AND listId=:listId AND bookmarked=1")
    suspend fun searchOnBookmark(query: String,listId: Int): List<Word>

    @Query("SELECT * FROM Word WHERE word LIKE '%' || :query || '%' AND (dateCreated BETWEEN :start AND :end) AND listId=:listId")
    suspend fun searchOnRecent(query: String, start: Long, end: Long, listId: Int): List<Word>

    @Query("UPDATE Word SET bookmarked=:isBookmarked WHERE id=:id")
    suspend fun isBookmark(isBookmarked: Boolean, id: Int)
}