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

    @Query("SELECT * FROM EnglishWords WHERE word LIKE :word || '%' ORDER BY word")
    fun search(word: String): PagingSource<Int, EnglishWords>

    @Query("SELECT COUNT(*) FROM EnglishWords")
    fun size(): LiveData<Int>
}