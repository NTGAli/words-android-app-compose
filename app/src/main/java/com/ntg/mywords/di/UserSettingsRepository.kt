package com.ntg.mywords.di

import com.ntg.mywords.UserDataAndSetting
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun getUserData(): Flow<UserDataAndSetting>
    suspend fun setUserEmail(email: String)
    suspend fun setUsername(name: String)
    suspend fun clearAllUserData()

}