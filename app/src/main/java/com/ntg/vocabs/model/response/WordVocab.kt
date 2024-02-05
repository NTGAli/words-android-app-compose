package com.ntg.vocabs.model.response

data class WordVocab(
    val word: String,
    val type: String?,
    val wordForms: WordForms?,
    val pronunciations: List<PronunciationVocab>,
    val definitions: List<Definition>,
    val idioms: List<Idiom>
)
