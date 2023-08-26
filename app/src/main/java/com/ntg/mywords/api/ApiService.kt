package com.ntg.mywords.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /**
     * VOCAB API KEY:
     * To access the VOCAB API, you'll need an API key.
     * For testing purposes, you can use the following token:
     * "TOKEN_TEST".
     * Please note that this token has limitations in terms of usage.
     *If you require a production-level API key or have specific usage requirements, please get in touch with us by emailing 'alintg@outlook.com'.
     */

    @GET("registration/verifyUser.php")
    suspend fun verifyUser(
        @Query("token") token: String,
        @Query("email") email: String
    ): Response<String>


    @GET("registration/VerifyCode.php")
    suspend fun verifyCode(
        @Query("token") token: String,
        @Query("email") email: String,
        @Query("code") code: String
    ): Response<String>


    @GET("registration/verifyPassword.php")
    suspend fun verifyPassword(
        @Query("token") token: String,
        @Query("email") email: String,
        @Query("pass") password: String
    ): Response<String>



}