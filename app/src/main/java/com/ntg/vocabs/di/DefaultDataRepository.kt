package com.ntg.vocabs.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.ntg.vocabs.UserDataAndSetting
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
            data.toBuilder().setEmail(email).build()
        }
    }

    override suspend fun setBackupOption(option: String) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setBackupOption(option).build()
        }
    }

    override suspend fun setBackupWay(way: String) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setBackupWay(way).build()
        }
    }

    override suspend fun setUsername(name: String) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setName(name).build()
        }
    }

    override suspend fun isSkipped(skip: Boolean) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setIsSkipped(skip).build()
        }
    }

    override suspend fun isIntroFinished(finished: Boolean) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setIsIntroFinished(finished).build()
        }
    }

    override suspend fun checkBackup(setBackup: Boolean) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setCheckBackup(setBackup).build()
        }
    }

    override suspend fun isSubscriptionSkipped(skipped: Boolean) {
        recentLocationsDataStore.updateData { data ->
            data.toBuilder().setIsSubscriptionSkipped(skipped).build()
        }
    }

    override suspend fun clearAllUserData() {
        recentLocationsDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }

    override suspend fun isUserPurchased(isPurchased: Boolean) {
        recentLocationsDataStore.updateData {
            it.toBuilder().setIsPurchased(isPurchased).build()
        }
    }

    override suspend fun isAllowThirdDictionary(allow: Boolean) {
        recentLocationsDataStore.updateData {
            it.toBuilder().setAllowThirdDictionary(allow).build()
        }
    }

    override suspend fun allowNotificationReminder(allow: Boolean) {
        recentLocationsDataStore.updateData {
            it.toBuilder().setNotificationReminder(allow).build()
        }
    }
}