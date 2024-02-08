package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ntg.vocabs.model.DriveBackup

@Dao
interface DriveBackupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verbs: List<DriveBackup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(backup: DriveBackup)

    @Query("SELECT * FROM DriveBackup ORDER BY id DESC")
    fun all(): LiveData<List<DriveBackup>>

    @Query("SELECT COUNT(*) FROM DriveBackup")
    fun size(): LiveData<Int>
}