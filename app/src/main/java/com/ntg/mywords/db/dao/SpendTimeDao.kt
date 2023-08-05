package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.mywords.model.db.SpendTime
import kotlinx.coroutines.flow.Flow

@Dao
interface SpendTimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spendTime: SpendTime)

    @Update
    suspend fun update(spendTime: SpendTime)

    @Query("SELECT * FROM SpendTime ORDER BY id DESC LIMIT 1")
    fun getLastItem(): LiveData<SpendTime>

    @Query("UPDATE SpendTime SET endUnix = :end WHERE id = ( SELECT id FROM SpendTime ORDER BY 1 DESC LIMIT 1)")
    suspend fun stopTime(end: Long)


//    @Query("SELECT * FROM SpendTime WHERE ")



}