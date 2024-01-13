package com.ntg.mywords.model.response

data class DictionaryResItem(
    val license: License?,
    val meanings: List<Meaning>?,
    val phonetic: String?,
    val phonetics: List<Phonetic>?,
    val sourceUrls: List<String>?,
    val word: String?
)