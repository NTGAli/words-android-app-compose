package com.ntg.mywords.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.VocabItemList

@Dao
interface VocabListDao {

    @Insert
    suspend fun insert(vocabList: VocabItemList)

    @Query("DELETE FROM VocabItemList")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<VocabItemList>)

    @Query("SELECT * FROM VocabItemList")
    fun getAllVocabList(): LiveData<List<VocabItemList>>

    @Query("UPDATE VocabItemList\n" +
            "SET isSelected = CASE\n" +
            "    WHEN id =:id THEN 1\n" +
            "    ELSE 0\n" +
            "END")
    suspend fun selectList(id: Int)

    @Query("SELECT * FROM VocabItemList WHERE isSelected=1")
    fun getDataOfListSelected(): LiveData<VocabItemList>

    @Query("SELECT COUNT(*) FROM VocabItemList WHERE title=:name AND language=:language")
    fun isExist(name: String, language: String): LiveData<Int>

}