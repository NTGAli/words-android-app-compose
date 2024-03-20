package com.ntg.vocabs.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ntg.vocabs.UserDataAndSetting
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.api.AuthorizeInterceptor
import com.ntg.vocabs.api.DictionaryApiService
import com.ntg.vocabs.api.FreeDictionaryApi
import com.ntg.vocabs.api.LoggingInterceptor
import com.ntg.vocabs.api.auth.AuthRepository
import com.ntg.vocabs.api.auth.AuthRepositoryImpl
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.db.dao.AdHistoryDao
import com.ntg.vocabs.db.dao.DriveBackupDao
import com.ntg.vocabs.db.dao.EnglishVerbDao
import com.ntg.vocabs.db.dao.EnglishWordDao
import com.ntg.vocabs.db.dao.GermanNounsDao
import com.ntg.vocabs.db.dao.GermanVerbsDao
import com.ntg.vocabs.db.dao.TimeSpentDao
import com.ntg.vocabs.db.dao.VocabListDao
import com.ntg.vocabs.db.dao.WordDao
import com.ntg.vocabs.util.Constant.DATABASE_NAME
import com.ntg.vocabs.util.Constant.DICTIONARY_API_URL
import com.ntg.vocabs.util.Constant.FREE_DICTIONARY_API_URL
import com.ntg.vocabs.util.Constant.VOCAB_API_URL
import com.ntg.vocabs.util.UserStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            .build()

    }

    @Provides
    @Singleton
    fun providesFirebaseAuth()  = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseDB()  = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun providesFireStore()  = Firebase.firestore


    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth):AuthRepository{
        return AuthRepositoryImpl(firebaseAuth)
    }


    @Provides
    @Singleton
    fun provideWordDao(appDB: AppDB): WordDao {
        return appDB.wordDao()
    }

    @Provides
    @Singleton
    fun provideDriveDao(appDB: AppDB): DriveBackupDao {
        return appDB.getDriveBackup()
    }

    @Provides
    @Singleton
    fun provideSpendTimeDao(appDB: AppDB): TimeSpentDao {
        return appDB.timeSpentDao()
    }

    @Provides
    @Singleton
    fun provideGermanNouns(appDB: AppDB): GermanNounsDao {
        return appDB.germanNounsDao()
    }

    @Provides
    @Singleton
    fun provideGermanVerbs(appDB: AppDB): GermanVerbsDao {
        return appDB.germanVerbsDao()
    }

    @Provides
    @Singleton
    fun provideVocabList(appDB: AppDB): VocabListDao {
        return appDB.vocabListDao()
    }

    @Provides
    @Singleton
    fun provideEnglishWordsDao(appDB: AppDB): EnglishWordDao {
        return appDB.getEnglishWordsDao()
    }

    @Provides
    @Singleton
    fun provideEnglishVerbsDao(appDB: AppDB): EnglishVerbDao {
        return appDB.getEnglishVerbsDao()
    }

    @Provides
    @Singleton
    fun provideAdDao(appDB: AppDB): AdHistoryDao {
        return appDB.getAddHistories()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor().httpLoggingInterceptor())
            .addInterceptor(AuthorizeInterceptor())
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
    @Named("FREE_DICTIONARY_API")
    fun provideFreeDictionaryRetrofit( okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FREE_DICTIONARY_API_URL)
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


    @Provides
    @Singleton
    fun provideFreeDictionaryApiService(@Named("FREE_DICTIONARY_API")retrofit: Retrofit): FreeDictionaryApi{
        return retrofit.create(FreeDictionaryApi::class.java)
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