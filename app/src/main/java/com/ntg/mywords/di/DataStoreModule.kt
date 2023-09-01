package com.ntg.mywords.di

import android.content.Context
import android.service.autofill.UserData
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//class DataStoreModule@Inject constructor(@ApplicationContext appContext: Context) {
//    private val settingsDataStore = appContext.dataStoreFile
//
//    suspend fun setThemeMode(mode: Int) {
//        settingsDataStore.edit { settings ->
//            settings[Settings.NIGHT_MODE] = mode
//        }
//    }
//
//    val themeMode: Flow<Int> = settingsDataStore.data.map { preferences ->
//        preferences[Settings.NIGHT_MODE] ?: AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
//    }
//}