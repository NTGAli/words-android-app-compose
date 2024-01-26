package com.ntg.mywords.model.data

data class VerbGermanForm(
    val simplePresent: List<GermanPronouns>? = null,
    val presentPerfect: List<GermanPronouns>? = null,
    val simplePast: List<GermanPronouns>? = null,
    val pastPerfect: List<GermanPronouns>? = null,
    val future_one: List<GermanPronouns>? = null,
    val future_two: List<GermanPronouns>? = null,
)
