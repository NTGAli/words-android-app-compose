package com.ntg.vocabs.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        private val THEME_APP = stringPreferencesKey("theme")
    }

    val getTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_APP] ?: "System default"
    }

    suspend fun saveTheme(token: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_APP] = token
        }
    }
}