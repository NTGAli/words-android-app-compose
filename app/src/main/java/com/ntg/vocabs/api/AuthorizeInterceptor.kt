package com.ntg.vocabs.api

import com.ntg.vocabs.BuildConfig.VOCAB_API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizeInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $VOCAB_API_KEY")
            .build()
        return chain.proceed(request)
    }

}