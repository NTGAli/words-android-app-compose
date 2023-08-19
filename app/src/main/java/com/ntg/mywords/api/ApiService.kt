package com.ntg.mywords.api

import com.ntg.mywords.model.response.WordData
import com.ntg.mywords.model.response.WordDataItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/v3/references/collegiate/json//{word}")
    suspend fun getDataWord(
        @Path("word") word: String,
        @Query("key") key: String
    ): Response<List<WordDataItem>>

}