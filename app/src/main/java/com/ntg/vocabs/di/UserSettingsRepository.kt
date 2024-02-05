package com.ntg.vocabs.di

import com.ntg.vocabs.UserDataAndSetting
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun getUserData(): Flow<UserDataAndSetting>
    suspend fun setUserEmail(email: String)
    suspend fun setUsername(name: String)
    suspend fun isSkipped(skip: Boolean)
    suspend fun clearAllUserData()

}