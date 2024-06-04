package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.vocabs.model.WeeklyWordCount
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

    @Query("UPDATE Word SET isDeleted=1 , synced=0 WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("UPDATE Word SET isDeleted=1 , synced=0 WHERE listId =:listId")
    suspend fun deleteWordOfList(listId: Int)

    @Query("DELETE FROM Word")
    suspend fun clear()

    @Query("SELECT * FROM Word WHERE isDeleted=0 ORDER BY id DESC")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE listId=:listId AND isDeleted=0 ORDER BY dateCreated DESC")
    fun getWordBaseListId(listId: Int): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE id =:id AND isDeleted=0")
    fun findWord(id: Int?): LiveData<Word>

    @Query("SELECT * FROM Word WHERE word =:word AND type = :type AND isDeleted=0")
    fun findWord(word: String, type: String): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE word =:word AND type = :type AND definition = :def")
    fun findWordWithDef(word: String, type: String, def: String): LiveData<List<Word>?>

    @Query("SELECT EXISTS(SELECT * FROM Word WHERE word =:word AND type =:type AND definition =:def AND isDeleted=0)")
    suspend fun isExist(word: String, type: String, def: String): Boolean

    @Query("SELECT COUNT(*) FROM Word WHERE (dateCreated BETWEEN :start AND :end) AND listId=:listId AND isDeleted=0")
    fun recentWordsCount(start: Long, end: Long, listId: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM Word WHERE isDeleted=0")
    fun size(): LiveData<Int>

    @Query("SELECT * FROM Word WHERE (word LIKE '%' || :query || '%') AND listId=:listId AND isDeleted=0")
    suspend fun search(query: String,listId: Int): List<Word>

    @Query("SELECT * FROM Word WHERE (word LIKE '%' || :query || '%') AND listId=:listId AND bookmarked=1 AND isDeleted=0")
    suspend fun searchOnBookmark(query: String,listId: Int): List<Word>

    @Query("SELECT * FROM Word WHERE word LIKE '%' || :query || '%' AND (dateCreated BETWEEN :start AND :end) AND listId=:listId AND isDeleted=0")
    suspend fun searchOnRecent(query: String, start: Long, end: Long, listId: Int): List<Word>

    @Query("UPDATE Word SET bookmarked=:isBookmarked WHERE id=:id AND isDeleted=0")
    suspend fun isBookmark(isBookmarked: Boolean, id: Int)

    @Query("SELECT * FROM Word WHERE listId=:listId AND isDeleted=0 ORDER BY RANDOM() LIMIT 20")
    fun randomWords(listId: Int): LiveData<List<Word>>

    @Query("SELECT * FROM Word WHERE synced=0")
    suspend fun getUnSyncedWords(): List<Word>

    @Query("UPDATE Word SET synced=1, fid=:fid WHERE id=:id")
    suspend fun synced(id: Int, fid: String)

    @Query("UPDATE Word SET synced=1 WHERE id=:id")
    suspend fun synced(id: Int)

    @Query("SELECT * FROM Word WHERE (voiceSynced NOT NULL OR imageSynced NOT NULL) AND (voiceSynced =0 OR imageSynced=0) AND isDeleted=0")
    suspend fun getUnSyncedMedia(): List<Word>

    @Query("UPDATE Word SET imageSynced=1 WHERE id=:id")
    suspend fun imageSynced(id: Int)

    @Query("UPDATE Word SET voiceSynced=1 WHERE id=:id")
    suspend fun voiceSynced(id: Int)

    @Query("""
        SELECT strftime('%W', datetime(dateCreated / 1000, 'unixepoch')) AS weekNumber, 
               COUNT(*) AS wordCount
        FROM Word
        WHERE listId=:listId AND isDeleted=0
        GROUP BY weekNumber
    """)
    fun getWordCountPerWeek(listId: Int): LiveData<List<WeeklyWordCount>>


    @Query("""
        SELECT COUNT(*) 
        FROM Word 
        WHERE dateCreated >= strftime('%s', date('now', 'weekday 0', '-7 days')) * 1000 
        AND dateCreated < strftime('%s', date('now', 'weekday 0')) * 1000
    """)
    fun getWordCountForCurrentWeek(): LiveData<Int>
}