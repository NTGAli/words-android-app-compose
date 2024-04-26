package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.vocabs.model.VocabsListWithCount
import com.ntg.vocabs.model.db.VocabItemList

@Dao
interface VocabListDao {

    @Insert
    suspend fun insert(vocabList: VocabItemList): Long

    @Update
    suspend fun update(vocabList: VocabItemList)

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

    @Query("SELECT * FROM VocabItemList WHERE id =:id")
    fun findList(id: Int?): LiveData<VocabItemList>

    @Query("DELETE FROM VocabItemList WHERE id =:id")
    suspend fun deleteById(id: Int?)

    @Query("SELECT * FROM VocabItemList WHERE isSelected=1")
    fun getDataOfListSelected(): LiveData<VocabItemList>

    @Query("SELECT * FROM VocabItemList WHERE synced=0")
    suspend fun getUnSyncedLists(): List<VocabItemList>

    @Query("UPDATE VocabItemList SET synced = 1 WHERE id=:id")
    suspend fun synced(id: Int)

    @Query("SELECT COUNT(*) FROM VocabItemList WHERE title=:name AND language=:language")
    fun isExist(name: String, language: String): LiveData<Int>

    @Query("SELECT VocabItemList.id, VocabItemList.title, VocabItemList.language, VocabItemList.isSelected, COUNT(Word.listId) AS countOfTableTwoItems FROM VocabItemList LEFT JOIN Word ON VocabItemList.id = Word.listId GROUP BY VocabItemList.id ORDER BY VocabItemList.id DESC")
    fun getListWithNumberOfWords(): LiveData<List<VocabsListWithCount>>

    @Query("UPDATE VocabItemList SET isSelected = CASE WHEN (SELECT COUNT(*) FROM VocabItemList WHERE isSelected = 0) = (SELECT COUNT(*) FROM VocabItemList) THEN 1 ELSE isSelected END WHERE id = (SELECT MIN(id) FROM VocabItemList)")
    suspend fun updateFirstItemIfAllNotSelected()

}