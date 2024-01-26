package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.mywords.model.db.GermanNouns

@Dao
interface GermanNounsDao {
    @Insert
    suspend fun insert(germanNoun: GermanNouns)

    @Update
    suspend fun update(germanNoun: GermanNouns)

    @Query("DELETE FROM GermanNouns")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<GermanNouns>)

    @Query("SELECT * FROM GermanNouns")
    fun getAllNouns(): LiveData<List<GermanNouns>>

    @Query("SELECT COUNT(*) FROM GermanNouns")
    fun size(): LiveData<Int>

    @Query("SELECT * FROM GermanNouns WHERE LOWER(lemma)=LOWER(:word)")
    fun findNoun(word: String): LiveData<GermanNouns>
}