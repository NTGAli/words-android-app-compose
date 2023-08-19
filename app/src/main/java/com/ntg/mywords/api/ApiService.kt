package com.ntg.mywords.api

import com.ntg.mywords.BuildConfig
import com.ntg.mywords.model.response.WordData
import com.ntg.mywords.model.response.WordDataItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    /**
     * DICTIONARY API KEY:
     * 1. Sign up at [https://www.dictionaryapi.com].
     * 2. Choose 'Merriam-Webster's Collegiate® Dictionary with Audio' plan.
     * 3. Get your API key from your account dashboard.
     * 4. Add `DICTIONARY_API_KEY="your_key_here"` to [gradle.properties].
     * 5. In [app/build.gradle], add to [defaultConfig]:
     *    ```
     *    buildConfigField("String", "DICTIONARY_API_KEY", DICTIONARY_API_KEY)
     *    ```
     * 6. Access using `BuildConfig.DICTIONARY_API_KEY`.
     */

    @GET("api/v3/references/collegiate/json//{word}")
    suspend fun getDataWord(
        @Path("word") word: String,
        @Query("key") key: String = BuildConfig.DICTIONARY_API_KEY
    ): Response<List<WordDataItem>>

}