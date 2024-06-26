package com.ntg.vocabs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ntg.vocabs.model.VocabsListWithCount
import com.ntg.vocabs.model.db.VocabItemList

@Dao
interface VocabListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vocabList: VocabItemList): Long

    @Update
    suspend fun update(vocabList: VocabItemList)

    @Query("DELETE FROM VocabItemList")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<VocabItemList>)

    @Query("SELECT * FROM VocabItemList WHERE isDeleted=0")
    fun getAllVocabList(): LiveData<List<VocabItemList>>

    @Query("UPDATE VocabItemList\n" +
            "SET isSelected = CASE\n" +
            "    WHEN id =:id THEN 1\n" +
            "    ELSE 0\n" +
            "END")
    suspend fun selectList(id: Int)

    @Query("SELECT * FROM VocabItemList WHERE id =:id AND isDeleted=0")
    fun findList(id: Int?): LiveData<VocabItemList>

    @Query("UPDATE VocabItemList SET isDeleted=1, synced=0, isSelected=0  WHERE id=:id")
    suspend fun deleteById(id: Int?)

    @Delete
    suspend fun delete(vocabList: VocabItemList)

    @Query("SELECT * FROM VocabItemList WHERE isSelected=1 AND isDeleted=0")
    fun getDataOfListSelected(): LiveData<VocabItemList>

    @Query("SELECT * FROM VocabItemList WHERE synced=0")
    suspend fun getUnSyncedLists(): List<VocabItemList>

    @Query("UPDATE VocabItemList SET synced=1, fid=:fid WHERE id=:id")
    suspend fun synced(id: Int, fid: String)

    @Query("UPDATE VocabItemList SET synced = 1 WHERE id=:id")
    suspend fun synced(id: Int)

    @Query("SELECT COUNT(*) FROM VocabItemList WHERE title=:name AND language=:language AND isDeleted=0")
    fun isExist(name: String, language: String): LiveData<Int>

    @Query("SELECT VocabItemList.id, VocabItemList.title, VocabItemList.language, VocabItemList.isSelected, COUNT(Word.listId) AS countOfTableTwoItems \n" +
            "FROM VocabItemList \n" +
            "LEFT JOIN Word ON VocabItemList.id = Word.listId \n" +
            "WHERE VocabItemList.isDeleted=0\n" +
            "GROUP BY VocabItemList.id \n" +
            "ORDER BY VocabItemList.id DESC;")
    fun getListWithNumberOfWords(): LiveData<List<VocabsListWithCount>>

    @Query("UPDATE VocabItemList SET isSelected = CASE WHEN (SELECT COUNT(*) FROM VocabItemList WHERE isSelected = 0 AND isDeleted=0) = (SELECT COUNT(*) FROM VocabItemList WHERE isDeleted=0) THEN 1 ELSE isSelected END WHERE id = (SELECT MIN(id) FROM VocabItemList)")
    suspend fun updateFirstItemIfAllNotSelected()

}