package com.ntg.mywords.model.response

data class Meaning(
    val antonyms: List<String>?,
    val definitions: List<DefinitionX>?,
    val partOfSpeech: String?,
    val synonyms: List<String>?
)