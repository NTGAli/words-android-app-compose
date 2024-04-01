package com.ntg.vocabs.di

import com.ntg.vocabs.UserDataAndSetting
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun getUserData(): Flow<UserDataAndSetting>
    suspend fun setUserEmail(email: String)
    suspend fun setBackupOption(option: String)
    suspend fun setBackupWay(way: String)
    suspend fun setUsername(name: String)
    suspend fun isSkipped(skip: Boolean)
    suspend fun isIntroFinished(finished: Boolean)
    suspend fun isSubscriptionSkipped(skipped: Boolean)
    suspend fun clearAllUserData()

}