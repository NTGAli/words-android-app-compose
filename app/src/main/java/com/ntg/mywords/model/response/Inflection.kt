package com.ntg.mywords.model.response

import com.google.gson.annotations.SerializedName

data class Inflection(
    val `if`: String?,
    val ifc: String?,
    @SerializedName("prs")
    val pronunciations: List<Pronunciation>?
)