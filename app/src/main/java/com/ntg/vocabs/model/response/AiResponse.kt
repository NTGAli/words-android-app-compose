package com.ntg.vocabs.model.response

data class AiResponse(
    val isCorrect: Boolean? = false,
    val correct: String? = null,
    val mistakes: List<Mistake>? = null,
    val acceptable: Boolean? = null
)

data class Mistake(
    val type: String? = null,
    val position: List<Int>? = null,
    val correct: String? = null
)
