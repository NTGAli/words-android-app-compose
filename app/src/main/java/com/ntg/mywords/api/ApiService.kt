package com.ntg.mywords.api

import com.ntg.mywords.model.req.BackupUserData
import com.ntg.mywords.model.req.VerifyUserReq
import com.ntg.mywords.model.response.DataRes
import com.ntg.mywords.model.response.RecentMessage
import com.ntg.mywords.model.response.ResponseBody
import com.ntg.mywords.model.response.VerifyUserRes
import com.ntg.mywords.model.response.WordVocab
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    @GET("registration/LoginWithEmail.php")
    suspend fun loginWithEmail(
        @Query("token") token: String,
        @Query("email") email: String
    ): Response<ResponseBody<Nothing>>


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
    ): Response<ResponseBody<VerifyUserRes>>

    @FormUrlEncoded
    @POST("registration/verifyByGoogle.php")
    suspend fun verifyByGoogle(
        @Field("token") token: String,
        @Field("email") email: String,
        @Field("name") name: String?,
        @Field("userId") userId: String?
    ): Response<ResponseBody<VerifyUserRes>>

    @GET("registration/VerifyName.php")
    suspend fun updateName(
        @Query("token") token: String,
        @Query("email") email: String,
        @Query("name") name: String
    ): Response<String>


    @POST("registration/updateEmail.php")
    @FormUrlEncoded
    suspend fun updateEmail(
        @Field("token") token: String,
        @Field("newEmail") newEmail: String,
        @Field("currentEmail") currentEmail: String,
    ): Response<String>


    @Multipart
    @POST("backup/Backup.php") // Replace with your server URL
    suspend fun uploadFile(
        @Part filePart: MultipartBody.Part
    )
    : Response<String>


    @POST("backup/Backup.php") // Replace with your server URL
    suspend fun backupUserData(
        @Body backupUserData: BackupUserData,
        @Query("email") email: String,
        @Query("token") token: String,
        @Query("ver") version: String
    ): Response<String>


    @FormUrlEncoded
    @POST("backup/restore.php")
    suspend fun restoreUserData(
        @Field("token") token: String,
        @Field("email") email: String,
    ): Response<ResponseBody<BackupUserData>>


    @FormUrlEncoded
    @POST("backup/lastBackup.php")
    suspend fun lastUserBackup(
        @Field("token") token: String,
        @Field("email") email: String,
    ): Response<String>


    @FormUrlEncoded
    @POST("registration/DeleteAccount.php")
    suspend fun deleteAccount(
        @Field("token") token: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<ResponseBody<Nothing>>

    @GET("DictionaryFiles/word.php")
    suspend fun getWord(
        @Query("word") word: String,
        @Query("type") type: String,
    ): Response<ResponseBody<WordVocab?>>

    @GET("MessageBox/recentMessages.php")
    suspend fun recentMessages()
    : Response<ResponseBody<List<RecentMessage>?>>


    @GET("Data/data.php")
    suspend fun germanDataList(
        @Query("lang") lang: String,
    ): Response<ResponseBody<List<DataRes>?>>


}