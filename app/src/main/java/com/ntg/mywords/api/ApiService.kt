package com.ntg.mywords.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/{word}")
    suspend fun getAllImages(
        @Path("word") page: Int,
        @Query("key") key: String
    ): Response<List<Feed>>

}