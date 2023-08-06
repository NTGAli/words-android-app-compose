package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.mywords.model.db.TimeSpent
import java.time.LocalDate

@Dao
interface TimeSpentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeSpent: TimeSpent)

    @Update
    suspend fun update(timeSpent: TimeSpent)

    @Query("SELECT * FROM TimeSpent ORDER BY id DESC LIMIT 1")
    fun getLastItem(): LiveData<TimeSpent>

    @Query("UPDATE TimeSpent SET endUnix = :end WHERE id = ( SELECT id FROM TimeSpent ORDER BY 1 DESC LIMIT 1)")
    suspend fun stopTime(end: Long)

    @Query("SELECT * FROM TimeSpent WHERE startUnix IS NOT NULL AND endUnix IS NOT NULL")
    fun getAllValidTime(): LiveData<List<TimeSpent>>

    @Query("SELECT * FROM TimeSpent WHERE type = 0 AND (startUnix IS NOT NULL AND endUnix IS NOT NULL)")
    fun getAllValidLearningTime(): LiveData<List<TimeSpent>>

    @Query("SELECT * FROM TimeSpent WHERE date = :date AND (startUnix IS NOT NULL AND endUnix IS NOT NULL)")
    fun getDtaOfDate(date: LocalDate): LiveData<List<TimeSpent>>


//    @Query("SELECT * FROM SpendTime WHERE ")



}