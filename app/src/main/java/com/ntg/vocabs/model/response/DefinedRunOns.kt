package com.ntg.vocabs.model.response

import com.google.gson.annotations.SerializedName

data class DefinedRunOns(
//    @SerializedName("def")
//    val definitionSections: List<DefItem>?,
    val drp: String?,
    @SerializedName("vrs")
    val variants: List<Variants>?
)