package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.vocabs.model.db.EnglishVerbs

@Dao
interface EnglishVerbDao {

    @Upsert
    suspend fun insertAll(verbs: List<EnglishVerbs>)

    @Query("SELECT * FROM EnglishVerbs WHERE word = :word LIMIT 1")
    fun find(word: String): LiveData<EnglishVerbs>

    @Query("SELECT COUNT(*) FROM EnglishVerbs")
    fun size(): LiveData<Int>

}