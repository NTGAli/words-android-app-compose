package com.ntg.mywords.model.response

import com.google.gson.annotations.SerializedName

data class UndefinedRunOns(
    @SerializedName("fl")
    val functionalLabel: String?,
    @SerializedName("prs")
    val pronunciations: List<Pronunciation>?,
    val ure: String?
)