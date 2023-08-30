package com.ntg.mywords.model.response

data class ResponseBody<T>(
    val isSuccess: Boolean? = null,
    val message: String? = null,
    val data:T? = null,
)
