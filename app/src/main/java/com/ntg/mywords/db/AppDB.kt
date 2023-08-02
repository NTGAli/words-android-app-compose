package com.ntg.mywords.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ntg.mywords.model.db.Word

@Database(entities = [Word::class], version = 2)
@TypeConverters(ExamplesConverters::class)
abstract class AppDB: RoomDatabase()  {

    abstract fun wordDao(): WordDao

}