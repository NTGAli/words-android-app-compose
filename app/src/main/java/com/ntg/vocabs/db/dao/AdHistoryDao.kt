package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ntg.vocabs.model.db.AdHistory
import com.ntg.vocabs.model.db.VocabItemList

@Dao
interface AdHistoryDao {

    @Insert
    suspend fun insert(ad: AdHistory): Long

    @Update
    suspend fun update(ad: AdHistory)

    @Query("DELETE FROM AdHistory")
    suspend fun clear()

    @Query("SELECT * FROM AdHistory WHERE aid=:id LIMIT 1")
    fun findAd(id: String): LiveData<AdHistory?>

}