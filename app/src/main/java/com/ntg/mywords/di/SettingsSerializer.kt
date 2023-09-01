package com.ntg.mywords.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ntg.mywords.UserDataAndSetting
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<UserDataAndSetting> {
    override val defaultValue: UserDataAndSetting = UserDataAndSetting.getDefaultInstance()
//
    override suspend fun readFrom(input: InputStream): UserDataAndSetting {
        try {
            return UserDataAndSetting.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
//
    override suspend fun writeTo(
        t: UserDataAndSetting,
        output: OutputStream
) = t.writeTo(output)
}

//val Context.settingsDataStore: DataStore<UserDataAndSetting> by dataStore(
//    fileName = "UserDataAndSetting.pb",
//    serializer = SettingsSerializer
//)