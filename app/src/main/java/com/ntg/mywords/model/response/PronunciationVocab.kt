package com.ntg.mywords.model.response

data class PronunciationVocab(
    val mp3: String?,
    val ogg: String,
    val accent: String?,
    val pronunciation: String?
)
