package com.ntg.mywords.api

import com.ntg.mywords.model.response.DictionaryResItem
import com.ntg.mywords.model.response.WordDataItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FreeDictionaryApi {

    @GET("/api/v2/entries/en/{word}")
    suspend fun getDataWord(
        @Path("word") word: String,
    ): Response<List<DictionaryResItem>?>
}