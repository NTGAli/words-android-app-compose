package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ntg.vocabs.model.db.EnglishWords

@Dao
interface EnglishWordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<EnglishWords>)

    @Query("SELECT * FROM EnglishWords WHERE word LIKE :word || '%' ORDER BY word LIMIT :limit OFFSET :offset")
    fun search(word: String, limit: Int, offset: Int): List<EnglishWords>

    @Query("SELECT COUNT(*) FROM EnglishWords")
    fun size(): LiveData<Int>
}