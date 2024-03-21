package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ntg.vocabs.model.db.AdHistory
import com.ntg.vocabs.model.db.EnglishWords
import com.ntg.vocabs.model.db.Sounds

@Dao
interface SoundDao {

    @Insert
    suspend fun insertAll(sounds: List<Sounds>)

    @Query("SELECT * FROM Sounds WHERE word =:word AND type=:type LIMIT 1")
    fun search(word: String, type: String): LiveData<Sounds>

    @Query("SELECT COUNT(*) FROM EnglishWords")
    fun size(): LiveData<Int>

}