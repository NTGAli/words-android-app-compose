package com.ntg.mywords.model.response

import com.google.gson.annotations.SerializedName

data class HeadwordInformation(
    val hw: String?,
    @SerializedName("prs")
    val pronunciations: List<Pronunciation?>?
)