package com.ntg.mywords.model

data class VerbData(
    val word: String,
    val past_simple: String? = null,
    val pp: String? = null,
    val ing: String
)
