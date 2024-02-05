package com.ntg.vocabs.api

import com.ntg.vocabs.model.response.DictionaryResItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FreeDictionaryApi {

    @GET("/api/v2/entries/en/{word}")
    suspend fun getDataWord(
        @Path("word") word: String,
    ): Response<List<DictionaryResItem>?>
}