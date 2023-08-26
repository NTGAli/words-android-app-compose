package com.ntg.mywords.api

import com.ntg.mywords.util.timber
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class LoggingInterceptor {
    fun httpLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor {
            timber("HttpLog: log: http log: $it")
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}