package com.ntg.mywords.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.util.timber
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultDataRepository @Inject constructor(
    private val context: Context,
    private val recentLocationsDataStore: DataStore<UserDataAndSetting>
) : DataRepository {
    override suspend fun getUserData(): Flow<UserDataAndSetting> {
        return recentLocationsDataStore.data

    }

    override suspend fun setUserEmail(email: String) {
        recentLocationsDataStore.updateData { data ->
            timber("skwelflkewjflkew $email")
            data.toBuilder().setEmail(email).build()
        }
    }

    override suspend fun setUsername(name: String) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setName(name).build()
        }
    }

    override suspend fun clearAllUserData() {
        recentLocationsDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }
}