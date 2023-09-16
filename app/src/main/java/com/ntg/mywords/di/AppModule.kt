package com.ntg.mywords.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.DictionaryApiService
import com.ntg.mywords.api.LoggingInterceptor
import com.ntg.mywords.db.AppDB
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.db.dao.VocabListDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.util.Constant
import com.ntg.mywords.util.Constant.DATABASE_NAME
import com.ntg.mywords.util.Constant.DATA_STORE_FILE_NAME
import com.ntg.mywords.util.Constant.DICTIONARY_API_URL
import com.ntg.mywords.util.Constant.PREFERENCE_DATA_STORE_NAME
import com.ntg.mywords.util.Constant.VOCAB_API_URL
import com.ntg.mywords.util.UserStore
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideAppDB(@ApplicationContext context: Context): AppDB{

        return Room.databaseBuilder(
            context = context,
            AppDB::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
//            .addTypeConverter(ExamplesConverters::class.java)
            .build()

    }


    @Provides
    @Singleton
    fun provideWordDao(appDB: AppDB): WordDao {
        return appDB.wordDao()
    }

    @Provides
    @Singleton
    fun provideSpendTimeDao(appDB: AppDB): TimeSpentDao {
        return appDB.timeSpentDao()
    }

    @Provides
    @Singleton
    fun provideVocabList(appDB: AppDB): VocabListDao {
        return appDB.vocabListDao()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor().httpLoggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @Named("DICTIONARY_API")
    fun provideRetrofit( okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DICTIONARY_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    @Named("VOCAB_API")
    fun provideVocabRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(VOCAB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionaryApiService(@Named("DICTIONARY_API")retrofit: Retrofit): DictionaryApiService{
        return retrofit.create(DictionaryApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideApiService(@Named("VOCAB_API")retrofit: Retrofit): ApiService{
        return retrofit.create(ApiService::class.java)
    }


    /**
     * For Proto Data Store
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    object RecentLocationsSerializer : Serializer<UserDataAndSetting> {
        override val defaultValue: UserDataAndSetting = UserDataAndSetting.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): UserDataAndSetting {
            try {
                return UserDataAndSetting.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                throw e
            }
        }

        override suspend fun writeTo(t: UserDataAndSetting, output: OutputStream) = t.writeTo(output)
    }

    private val Context.recentLocationsDataStore: DataStore<UserDataAndSetting> by dataStore(
        fileName = "RecentLocations.pb",
        serializer = RecentLocationsSerializer
    )

    @Provides
    @Reusable
    fun provideProtoDataStore(@ApplicationContext context: Context) =
        context.recentLocationsDataStore

    @Provides
    @Reusable
    internal fun providesDataRepository(
        @ApplicationContext context: Context,
        recentLocationsDataStore: DataStore<UserDataAndSetting>
    ): DataRepository {
        return DefaultDataRepository(
            context,
            recentLocationsDataStore
        )
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): UserStore {
        return UserStore(context)
    }

}