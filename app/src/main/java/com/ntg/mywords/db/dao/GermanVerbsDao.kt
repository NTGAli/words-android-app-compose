package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.mywords.model.db.GermanVerbs

@Dao
interface GermanVerbsDao {
    @Insert
    suspend fun insert(germanNoun: GermanVerbs)

    @Update
    suspend fun update(germanNoun: GermanVerbs)

    @Query("DELETE FROM GermanVerbs")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<GermanVerbs>)

    @Query("SELECT * FROM GermanVerbs")
    fun getAllNouns(): LiveData<List<GermanVerbs>>

    @Query("SELECT COUNT(*) FROM GermanVerbs")
    fun size(): LiveData<Int>

    @Query("SELECT * FROM GermanVerbs WHERE LOWER(word)=LOWER(:word)")
    fun findNoun(word: String): LiveData<GermanVerbs>
}