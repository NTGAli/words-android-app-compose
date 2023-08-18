package com.ntg.mywords.di

import android.content.Context
import android.os.Build
import androidx.room.Room
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.LoggingInterceptor
import com.ntg.mywords.db.AppDB
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.util.Constant.DATABASE_NAME
import com.ntg.mywords.util.Constant.DICTIONARY_API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


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
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor().httpLoggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DICTIONARY_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService{
        return retrofit.create(ApiService::class.java)
    }


}