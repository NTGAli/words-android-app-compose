package com.ntg.vocabs.api

import com.ntg.vocabs.util.timber
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor {
    fun httpLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor {
            timber("HttpLog: log: http log: $it")
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}