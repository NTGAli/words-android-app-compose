package com.ntg.vocabs.model.data

data class VerbGermanForm(
    var title: String? = null,
    val simplePresent: List<GermanPronouns>? = null,
    val presentPerfect: List<GermanPronouns>? = null,
    val simplePast: List<GermanPronouns>? = null,
    val pastPerfect: List<GermanPronouns>? = null,
    val future_one: List<GermanPronouns>? = null,
    val future_two: List<GermanPronouns>? = null,
)
