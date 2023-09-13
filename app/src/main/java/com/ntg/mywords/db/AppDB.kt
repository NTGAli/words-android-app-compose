package com.ntg.mywords.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ntg.mywords.db.converters.DateConverter
import com.ntg.mywords.db.converters.ExamplesConverters
import com.ntg.mywords.db.converters.VerbFormsConverter
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.db.dao.VocabListDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.model.db.Word

@Database(entities = [Word::class, TimeSpent::class, VocabItemList::class], version = 17)
@TypeConverters(ExamplesConverters::class, VerbFormsConverter::class, DateConverter::class)
abstract class AppDB: RoomDatabase()  {

    abstract fun wordDao(): WordDao
    abstract fun timeSpentDao(): TimeSpentDao
    abstract fun vocabListDao(): VocabListDao

}