package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.vocabs.model.db.TimeSpent
import java.time.LocalDate

@Dao
interface TimeSpentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeSpent: TimeSpent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeSpent: List<TimeSpent>)

    @Update
    suspend fun update(timeSpent: TimeSpent)

    @Query("DELETE FROM TimeSpent WHERE  listId =:listId")
    suspend fun deleteTimeOfList(listId: Int)

    @Query("DELETE FROM TimeSpent")
    suspend fun clear()

    @Query("DELETE FROM TimeSpent WHERE endUnix IS NULL")
    suspend fun removeNullTime()

    @Query("SELECT * FROM TimeSpent ORDER BY id DESC LIMIT 1")
    fun getLastItem(): LiveData<TimeSpent>

    @Query("UPDATE TimeSpent SET endUnix =:end WHERE id = ( SELECT id FROM TimeSpent ORDER BY 1 DESC LIMIT 1)")
    suspend fun stopTime(end: Long)

    @Query("SELECT * FROM TimeSpent WHERE startUnix IS NOT NULL AND endUnix IS NOT NULL AND listId=:listId")
    fun getAllValidTimeBaseListId(listId: Int): LiveData<List<TimeSpent>>

    @Query("SELECT * FROM TimeSpent WHERE startUnix IS NOT NULL AND endUnix IS NOT NULL")
    fun getAllValidTime(): LiveData<List<TimeSpent>>

    @Query("SELECT * FROM TimeSpent WHERE type = :type AND startUnix IS NOT NULL AND endUnix IS NOT NULL AND listId=:listId ORDER BY id DESC")
    fun getAllValidTimesBaseType(type: Int, listId: Int): LiveData<List<TimeSpent>>

    @Query("SELECT * FROM TimeSpent WHERE type = 0 AND (startUnix IS NOT NULL AND endUnix IS NOT NULL)")
    fun getAllValidLearningTime(): LiveData<List<TimeSpent>>


    @Query("SELECT * FROM TimeSpent WHERE type = :type AND date = :date AND (startUnix IS NOT NULL AND endUnix IS NOT NULL) AND listId=:listId")
    fun getDtaOfDate(date: LocalDate, type: Int, listId: Int): LiveData<List<TimeSpent>>

    @Query("SELECT * FROM TimeSpent WHERE synced=0")
    fun getUnSyncedTime(): List<TimeSpent>

    @Query("UPDATE TimeSpent SET synced=1 WHERE id=:id")
    fun synced(id: Int)


}