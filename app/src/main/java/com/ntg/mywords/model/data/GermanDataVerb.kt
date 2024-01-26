package com.ntg.mywords.model.data

data class GermanDataVerb(
    val indicative: VerbGermanForm? = null,
    val conjunctive: VerbGermanForm? = null,
    val conditional: VerbGermanForm? = null,
    val imperative: List<GermanPronouns>? = null,
)
