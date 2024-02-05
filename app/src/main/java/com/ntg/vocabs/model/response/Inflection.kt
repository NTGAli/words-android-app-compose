package com.ntg.vocabs.model.response

import com.google.gson.annotations.SerializedName

data class Inflection(
    @SerializedName("if")
    val infection: String?,
    val ifc: String?,
    @SerializedName("prs")
    val pronunciations: List<Pronunciation>?
)