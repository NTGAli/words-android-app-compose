package com.ntg.vocabs.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ntg.vocabs.db.converters.DateConverter
import com.ntg.vocabs.db.converters.ExamplesConverters
import com.ntg.vocabs.db.converters.GermanDataVerbConverter
import com.ntg.vocabs.db.converters.GermanVerbConverter
import com.ntg.vocabs.db.converters.VerbFormsConverter
import com.ntg.vocabs.db.dao.AdHistoryDao
import com.ntg.vocabs.db.dao.DriveBackupDao
import com.ntg.vocabs.db.dao.EnglishVerbDao
import com.ntg.vocabs.db.dao.EnglishWordDao
import com.ntg.vocabs.db.dao.GermanNounsDao
import com.ntg.vocabs.db.dao.GermanVerbsDao
import com.ntg.vocabs.db.dao.SoundDao
import com.ntg.vocabs.db.dao.TimeSpentDao
import com.ntg.vocabs.db.dao.VocabListDao
import com.ntg.vocabs.db.dao.WordDao
import com.ntg.vocabs.model.DriveBackup
import com.ntg.vocabs.model.db.AdHistory
import com.ntg.vocabs.model.db.EnglishVerbs
import com.ntg.vocabs.model.db.EnglishWords
import com.ntg.vocabs.model.db.GermanNouns
import com.ntg.vocabs.model.db.GermanVerbs
import com.ntg.vocabs.model.db.Sounds
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.db.Word

@Database(entities = [Word::class, TimeSpent::class, VocabItemList::class, GermanNouns::class, GermanVerbs::class, EnglishWords::class, EnglishVerbs::class, DriveBackup::class, AdHistory::class, Sounds::class], version = 1)
@TypeConverters(ExamplesConverters::class, VerbFormsConverter::class, DateConverter::class, GermanVerbConverter::class, GermanVerbConverter::class, GermanDataVerbConverter::class)
abstract class AppDB: RoomDatabase()  {

    abstract fun wordDao(): WordDao
    abstract fun timeSpentDao(): TimeSpentDao
    abstract fun vocabListDao(): VocabListDao
    abstract fun germanNounsDao(): GermanNounsDao
    abstract fun germanVerbsDao(): GermanVerbsDao
    abstract fun getEnglishWordsDao(): EnglishWordDao
    abstract fun getEnglishVerbsDao(): EnglishVerbDao
    abstract fun getDriveBackup(): DriveBackupDao
    abstract fun getAddHistories(): AdHistoryDao
    abstract fun getSoundsDao(): SoundDao

}