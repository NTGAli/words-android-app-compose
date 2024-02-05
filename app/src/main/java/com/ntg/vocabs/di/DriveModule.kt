package com.ntg.vocabs.di

import com.ntg.vocabs.api.auth.AuthRepository
import com.ntg.vocabs.api.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent

//@Module
//@InstallIn(ActivityRetainedComponent::class)
//abstract class DriveModule {
//    @Binds
//    @ActivityRetainedScoped
//    abstract fun bindAuthRepo(
//        authRepository: AuthRepositoryImpl
//    ): AuthRepository
//}