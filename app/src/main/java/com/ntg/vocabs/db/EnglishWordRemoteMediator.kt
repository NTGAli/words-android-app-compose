package com.ntg.vocabs.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ntg.vocabs.model.db.EnglishWords

//@OptIn(ExperimentalPagingApi::class)
//class EnglishWordRemoteMediator: RemoteMediator<Int, EnglishWords>() {
//
//
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, EnglishWords>
//    ): MediatorResult {
//
//    }
//}